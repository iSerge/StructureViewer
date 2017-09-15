package org.xraycrystal;

import org.jetbrains.annotations.NotNull;

public class UnitCell {
    public final double a;
    public final double b;
    public final double c;
    public final double alpha;
    public final double beta;
    public final double gamma;

    public UnitCell(double a, double b, double c, double alpha, double beta, double gamma){
        this.a = a;
        this.b = b;
        this.c = c;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    @NotNull
    public static UnitCell cubic(double a){
        return new UnitCell(a, a, a, Math.PI/2, Math.PI/2, Math.PI/2);
    }

    @NotNull static UnitCell triclinic(double a, double b, double c, double alpha, double beta, double gamma){
        return new UnitCell(a, b, c, alpha, beta, gamma);
    }

    @NotNull static UnitCell monoclinic(double a, double b, double c, double beta){
        return new UnitCell(a, b, c, Math.PI/2, beta, Math.PI/2);
    }

    @NotNull
    public static UnitCell orthorhombic(double a, double b, double c){
        return new UnitCell(a, b, c, Math.PI/2, Math.PI/2, Math.PI/2);
    }

    @NotNull
    public static UnitCell tetragonal(double a, double c){
        return new UnitCell(a, a, c, Math.PI/2, Math.PI/2, Math.PI/2);
    }

    @NotNull
    public static UnitCell rhombohedral(double a, double alpha){
        return new UnitCell(a, a, a, alpha, alpha, alpha);
    }

    @NotNull
    public static UnitCell hexagonal(double a, double c){
        return new UnitCell(a, a, c, Math.PI/2, Math.PI/2, 2*Math.PI/3);
    }
}
