1;

a = 1;
b = 1;
c = 1;

alpha = .7 * pi / 2; % angle between b and c
beta = .7 * pi / 2; % angle between a and c
gamma = .8 * pi / 2; % angle between a and b

h = 0;
k = 2;
l = 1;

cz = c *sqrt(1 - cos(alpha)^2 - cos(beta)^2 - cos(gamma)^2 + ...
    2*cos(alpha)*cos(beta)*cos(gamma))/sin(gamma);

M = [a              0                                                  0;
     b * cos(gamma) b * sin(gamma)                                     0;
     c * cos(beta)  c * (cos(alpha) - cos(beta)*cos(gamma))/sin(gamma) cz];

n = 2;

X = zeros(n^3,1);
Y = zeros(n^3,1);
Z = zeros(n^3,1);
i = 1;

for x = 0:n-1
    for y = 0:n-1
        for z = 0:n-1
            X(i) = x * M(1,1) + y * M(2,1) + z * M(3,1);
            Y(i) = x * M(1,2) + y * M(2,2) + z * M(3,2);
            Z(i) = x * M(1,3) + y * M(2,3) + z * M(3,3);
            i = i + 1;
        end
    end
end

if 0 == h
    if 0 ~= k
        A = M(2,:)/k + M(1,:);
    else
        A = M(3,:)/l + M(1,:);
    end
else
    A = M(1,:) / h;
end
if 0 == k
    if 0 ~= h
        B = M(1,:)/h + M(2,:);
    else
        B = M(3,:)/l + M(2,:);
    end
else
    B = M(2,:) / k;
end
if 0 == l
    if 0 ~= h
        C = M(1,:)/h + M(3,:);
    else
        C = M(2,:)/k + M(3,:);
    end
else
    C = M(3,:) / l;
end

AB = B - A;
AC = C - A;

N = [AB(2)*AC(3) - AB(3)*AC(2)
     AB(3)*AC(1) - AB(1)*AC(3)
     AB(1)*AC(2) - AB(2)*AC(1)];

[X1, Y1] = meshgrid(-1:1:1);
Z1 = -1/N(3)*(N(1)*X1 + N(2)*Y1);

N = N/norm(N);

d = A*N;

X1 = X1 + N(1)*d;
Y1 = Y1 + N(2)*d;
Z1 = Z1 + N(3)*d;

fig = figure();
ax = axes('Parent', fig);

hold on;

scatter3(X,Y,Z, 'filled', 'MarkerFaceColor',[0 .5 1], 'Parent', ax);

scatter3(M(1,1) + M(2,1) + M(3,1), M(1,2) + M(2,2) + M(3,2), M(1,3) + M(2,3) + M(3,3), ...
    'filled', 'MarkerFaceColor',[1 .7 0], 'Parent', ax);


scatter3(M(1,1), M(1,2), M(1,3), 'filled', 'MarkerFaceColor',[1 0 0], 'Parent', ax);
scatter3(M(2,1), M(2,2), M(2,3), 'filled', 'MarkerFaceColor',[0 1 0], 'Parent', ax);
scatter3(M(3,1), M(3,2), M(3,3), 'filled', 'MarkerFaceColor',[0 0 1], 'Parent', ax);

surf(X1,Y1,Z1);
patch([A(1) B(1) C(1)] , [A(2) B(2) C(2)], [A(3) B(3) C(3)], [.5 .5 .5]);

axis equal;
hold off;
