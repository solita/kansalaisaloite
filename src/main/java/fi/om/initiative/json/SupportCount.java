package fi.om.initiative.json;

public class SupportCount {
    private int supportCount;
    private int externalSupportCount;

    public SupportCount(int supportCount, int externalSupportCount) {
        this.supportCount = supportCount;
        this.externalSupportCount = externalSupportCount;
    }

    public int getSupportCount() {
        return supportCount;
    }

    public int getExternalSupportCount() {
        return externalSupportCount;
    }

    public int getTotalSupportCount() {
        return supportCount + externalSupportCount;
    }
}
