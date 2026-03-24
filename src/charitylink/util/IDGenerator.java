package charitylink.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger donorCounter    = new AtomicInteger(1000);
    private static final AtomicInteger donationCounter = new AtomicInteger(5000);

    public static String nextDonorId()    { return "D" + donorCounter.getAndIncrement(); }
    public static String nextDonationId() { return "DON" + donationCounter.getAndIncrement(); }

    public static void setDonorSeed(int seed)    { donorCounter.set(seed); }
    public static void setDonationSeed(int seed) { donationCounter.set(seed); }
}
