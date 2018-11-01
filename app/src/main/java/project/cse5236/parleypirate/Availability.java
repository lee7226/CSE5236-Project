package project.cse5236.parleypirate;

public class Availability {
    private String userId;
    private String availability;

    public String GetOpenAvalability(String[] avals) {
        String openAval = "";
        int[] intRepresentations = new int[avals.length];

        // convert all given avals to int then to binary
        for (int i = 0; i < avals.length; i++) {
            intRepresentations[i] = Integer.parseInt(avals[i], 2);
        }

        int bitwisedAvals = 0;

        // do bitwise or on binary avals
        for (int i = 0; i < intRepresentations.length; i++) {
            bitwisedAvals = bitwisedAvals | intRepresentations[i];
        }
        // set openAvals to the result
        openAval = Integer.toString(bitwisedAvals, 2);

        return  openAval;
    }
}
