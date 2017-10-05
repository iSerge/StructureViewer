function r = d_space(cell_type)
    if (strcmp('cubic', cell_type))
        r = @(h,k,l,a,b,c,alpha,beta,gamma) (h^2 + k^2 + l^2)/(a^2);
    elseif (strcmp('tetragonal', cell_type))
        r = @(h,k,l,a,b,c,alpha,beta,gamma) (h^2 + k^2)/(a^2) + (l^2)/(c^2);
    elseif (strcmp('hexagonal', cell_type))
        r = @(h,k,l,a,b,c,alpha,beta,gamma) (4.0/3.0)*(h^2 + h*k + k^2)/(a^2) + (l^2)/(c^2);
    elseif (strcmp('rhomohedral', cell_type))
        r = @(h,k,l,a,b,c,alpha,beta,gamma) ((h^2 + k^2 + l^2) * sin(alpha)^2 + ...
            2*(h*k + k*l + h*l)*(cos(alpha)^2 - cos(alpha)))/((a^2)*(1 - 3*cos(alpha)^2 + 2*cos(alpha)^3));
    elseif (strcmp('orthorhombic', cell_type))
            r = @(h,k,l,a,b,c,alpha,beta,gamma) (h^2)/(a^2) + (k^2)/(b^2) + (l^2)/(c^2);
    elseif (strcmp('monoclinic', cell_type))
            r = @(h,k,l,a,b,c,alpha,beta,gamma) h^2/(a^2*sin(beta)^2) + k^2/(b^2) + l^2 /(c^2*sin(beta)^2) ...
                - 2*h*l*cos(beta)/(a * c*sin(beta)^2);
    elseif (strcmp('triclinic', cell_type))
            r = @(h,k,l,a,b,c,alpha,beta,gamma) ((h^2*sin(alpha)^2/(a^2)) + (k^2*sin(beta)^2/(b^2)) + ...
                (l^2*sin(gamma)^2/(c^2)) + ...
                ((2*k*l*cos(alpha))/(b*c)) + ((2*h*l*cos(beta))/(a*c)) + ((2*h*k*cos(gamma))/(a*b))) / ...
                (1 - cos(alpha)^2 - cos(beta)^2 - cos(gamma)^2 + 2*cos(alpha)*cos(beta)*cos(gamma));
    end
end