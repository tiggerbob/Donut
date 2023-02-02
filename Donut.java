import java.util.Arrays;

public class Donut {
    public static void main(String[] args) {
        final int screen_width = 45;
        final int screen_height = 45;
        final int screen_size = screen_width * screen_height;

        final double pi = 3.14;

        // theta: variable degrees of circle
        // phi: variable degrees of torus formation
        final double theta_spacing = 0.07;
        final double phi_spacing   = 0.02;

        // R1: radius of circle
        // R2: distance to center of donut
        // K2: distance of donut to viewer
        // K1: proportion of screen to object (z prime)
        final int R1 = 1;
        final int R2 = 2;
        final int K2 = 7;
        final double K1 = (screen_width*K2*3)/(8*(R1+R2));

        // A: x rotation
        // B: z rotation
        double A = 0, B = 0;

        char[] output = new char[screen_size];
        double[] zbuffer = new double[screen_size];

        while(true) {
            Arrays.fill(output, ' ');
            Arrays.fill(zbuffer, 0);
            for (double theta = 0; theta < 2*pi; theta += theta_spacing) {
                for (double phi = 0; phi < 2*pi; phi += phi_spacing) {
                    double sinphi = Math.sin(phi),
                            costheta = Math.cos(theta),
                            sinA = Math.sin(A),
                            sintheta = Math.sin(theta),
                            cosA = Math.cos(A),
                            circlex = (R1*costheta) + R2,
                            circley = R1*sintheta,
                            // one over z
                            ooz = 1 / ((sinphi * circlex * cosA) + (circley * sinA + K2)),
                            cosphi = Math.cos(phi),
                            cosB = Math.cos(B),
                            sinB = Math.sin(B);

                    // final coords after rotations
                    double x = circlex*(cosB*cosphi + sinA*sinB*sinphi) - circley*cosA*sinB; 
                    double y = circlex*(sinB*cosphi - sinA*cosB*sinphi) + circley*cosA*cosB;

                    // x and y projection
                    int xp = (int) (screen_width/2 + (K1*1.1)*ooz*x);
                    int yp = (int) (screen_height/3 - (K1*0.7)*ooz*y);
                    
                    // luminance
                    double L = cosphi*costheta*sinB - cosA*costheta*sinphi - sinA*sintheta + cosB*(cosA*sintheta - costheta*sinA*sinphi);

                    // translate 2d space into 1d character array
                    int index = xp + screen_height*yp;
      
                    if(L > 0) {
                        if(index < screen_size && index >= 0 && ooz > zbuffer[index]) {
                            zbuffer[index] = ooz;
                            int luminance_index = (int) (L*8);
                            // .,-~*:;!+#$@
                            output[index] = new char[]{'.', ',', '-', '~', '*', ':', ';', '!', '+', '#', '$', '@'}[Math.max(luminance_index, 0)];
                        }
                    }
                }
            }

            // bring cursor to home loc 
            System.out.print("\u001b[H");
            for (int k = 0; k <= screen_size; k++)
                System.out.print(k % screen_height > 0 ? output[k] : "\n");
            A += 0.005;
            B += 0.003;
        }
    }
}