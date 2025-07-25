package trxsh.ontop.crystalbot.util;

import org.bukkit.Location;

public class ProjectileUtility {
    public static double[] calculateAngles(Location i, Location f) {
        double dx = f.getX() - i.getX();
        double dy = f.getY() - i.getY();
        double dz = f.getZ() - i.getZ();

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double theta = Math.atan2(dz, dx) - Math.PI / 2;

        double launchAngle = findLaunchAngle(horizontalDistance, dy);

        double pitch = -launchAngle;

        double yaw = Math.toDegrees(theta);
        double pitchDegrees = Math.toDegrees(pitch);

        return new double[] {yaw, pitchDegrees};
    }

    /*
    The following pseudocode from this forum: https://www.spigotmc.org/threads/calculate-launch-angle-of-projectile.599849/
    I hate doing math.
     */

    private static final double GRAVITY = -0.98;
    private static final double FULL_POWER_VELOCITY = 53.0;
    private static final double DRAG_COEFFICIENT = 0.99;

    private static double findLaunchAngle(double horizontalDistance, double deltaY) {
        double angle1 = 0;
        double angle2 = Math.PI / 2;

        double epsilon = 0.00001;
        double angle = 0;

        while (Math.abs(angle2 - angle1) > epsilon) {
            angle = (angle1 + angle2) / 2;
            double yDiff = deltaY - calculateVerticalDistance(horizontalDistance, angle);

            if (yDiff > 0) {
                angle1 = angle;
            } else {
                angle2 = angle;
            }
        }

        return angle;
    }

    private static double calculateVerticalDistance(double horizontalDistance, double launchAngle) {
        double initialHorizontalVelocity = FULL_POWER_VELOCITY * Math.cos(launchAngle);

        double verticalVelocity = FULL_POWER_VELOCITY * Math.sin(launchAngle);
        double verticalDistance = 0;

        double horizontalVelocity = initialHorizontalVelocity;
        double currentHorizontalDistance = 0;

        while (currentHorizontalDistance < horizontalDistance) {
            double deltaTime = 0.05;
            horizontalVelocity *= DRAG_COEFFICIENT;
            currentHorizontalDistance += horizontalVelocity * deltaTime;

            verticalVelocity = (verticalVelocity + GRAVITY * deltaTime) * DRAG_COEFFICIENT;
            verticalDistance += verticalVelocity * deltaTime;
        }

        return verticalDistance;
    }
}
