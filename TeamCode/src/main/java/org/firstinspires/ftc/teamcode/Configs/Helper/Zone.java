
package org.firstinspires.ftc.teamcode.Configs.Helper;

public class Zone {

    // ═════════════════════════ CONSTANTS ═════════════════════════


    // ═════════════════════════ METHODS ═════════════════════════

    // Checks whether a point is inside a polygon
    // corners = {x0, y0, x1, y1, x2, y2, ...}
    public static boolean contains(double[] corners, double x, double y) {
        boolean hasPositive = false;
        boolean hasNegative = false;
        int count = corners.length / 2;

        //region Check polygon edges
        for (int i = 0; i < count; i++) {
            int j = (i + 1) % count;

            double x1 = corners[i * 2], y1 = corners[i * 2 + 1];
            double x2 = corners[j * 2], y2 = corners[j * 2 + 1];

            double cross = (x2 - x1) * (y - y1) - (y2 - y1) * (x - x1);

            if (cross > 0) hasPositive = true;
            if (cross < 0) hasNegative = true;
            if (hasPositive && hasNegative) return false;
        }
        //endregion

        return true;
    }
}
