package testessentials.dbunit;

import org.dbunit.dataset.ReplacementDataSet;

public class Replacements {

    public static ReplacementDataSet addReplacements(ReplacementDataSet replacementDataSet) {
        replacementDataSet.addReplacementObject("###NULL###", null);
        replacementDataSet.addReplacementObject("###LN###", "\n");
        return replacementDataSet;
    }
}
