package opt.test;

import opt.test.FlipFlopTest;
import opt.test.ContinuousPeaksTest;
import opt.test.FourPeaksTest;
import opt.test.CountOnesTest;

public class RunAll {
    public static void main(String[] args) {
        FlipFlopTest ff = new FlipFlopTest();
        ff.main(new String[] {""});

        ContinuousPeaksTest cp = new ContinuousPeaksTest();
        cp.main(new String[] {""});

        FourPeaksTest fp = new FourPeaksTest();
        fp.main(new String[] {""});

        CountOnesTest co = new CountOnesTest();
        co.main(new String[] {""});
    }
}
