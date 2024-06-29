import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        double[] environmentCurrentStates = {2, 2.7, 4, 6, 3, 32};
        double[] environmentStatesDOF = {2, 2.7, 4, 6, 3, 32}; // DOF <--> Degree of Freedom
        double[] machineCurrentStates = {3, 6};
        double[] machineStatesDOF = {10, 10};
        double[] machineNextState = new double[machineCurrentStates.length],
                environmentNextStates = new double[environmentCurrentStates.length];
        double[] machineInput, machineOutput;
        int evolutionLength = 100;

        for (int j = 1; j <= evolutionLength; j++) {
            System.out.println("Evolution " + j);
            System.out.println("Environment current states: " + Arrays.toString(environmentCurrentStates));
            System.out.println("Machine current states: " + Arrays.toString(machineCurrentStates));
            machineInput = machineInput(machineCurrentStates, environmentCurrentStates);
            System.out.println("Machine input: " + Arrays.toString(machineInput));
            machineOutput = machineOutput(machineInput, machineStatesDOF, environmentStatesDOF);

            for (int i = 0; i < machineOutput.length; i++) {
                if (i < machineNextState.length) {
                    machineNextState[i] = machineOutput[i];
                } else if (i < environmentNextStates.length) {
                    environmentNextStates[i - machineNextState.length] = machineOutput[i];
                }
            }

            System.out.println("Machine next states: " + Arrays.toString(machineNextState));
            System.out.println("Machine States DOF: " + Arrays.toString(machineStatesDOF));
            System.out.println("Environment next states (prediction): " + Arrays.toString(environmentNextStates));
            System.out.println("Environment states DOF: " + Arrays.toString(environmentStatesDOF) +"\n");

            machineCurrentStates = machineNextState;
        }
    }

    public static double[] machineInput(double[] machineCurrentStates,
                                        double[] environmentCurrentStates)
    {
        double[] machineInput = new double[machineCurrentStates.length + environmentCurrentStates.length];

        // Copy machineCurrentStates to machineInput
        System.arraycopy(machineCurrentStates, 0, machineInput,
                0, machineCurrentStates.length);

        // Copy environmentCurrentStates to machineInput
        System.arraycopy(environmentCurrentStates, 0, machineInput,
                machineCurrentStates.length, environmentCurrentStates.length);

        return machineInput;
    }

    public static double[] machineOutput(double[] machineInput, double[] machineStatesDOF,
                                         double[] environmentStatesDOF)
    {
        int n = machineInput.length;
        double[] machineOutput = new double[n];
        double[][] squaredMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            machineOutput[i] = 0;
            for (int j = 0; j < n; j++) {
                squaredMatrix[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    // Generate Latin square element
                    double latinSquareElement = machineInput[(i + k) % n];

                    // Do squaring operation
                    squaredMatrix[i][j] += latinSquareElement * machineInput[(k + j) % n];
                }
                // Do dot product operation
                machineOutput[i] += squaredMatrix[i][j] * machineInput[j];
            }
            // Constrain next states
            if (i < machineStatesDOF.length) {
                machineOutput[i] = machineOutput[i] % machineStatesDOF[i];
            } else if (i < environmentStatesDOF.length) {
                machineOutput[i] = machineOutput[i] % environmentStatesDOF[i - machineStatesDOF.length];
            }
        }

        return machineOutput;
    }
}
