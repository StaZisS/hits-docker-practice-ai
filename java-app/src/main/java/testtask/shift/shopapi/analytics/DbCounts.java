package testtask.shift.shopapi.analytics;

public class DbCounts {
    private final long laptops;
    private final long monitors;
    private final long hardDrives;
    private final long personalComputers;

    public DbCounts(long laptops, long monitors, long hardDrives, long personalComputers) {
        this.laptops = laptops;
        this.monitors = monitors;
        this.hardDrives = hardDrives;
        this.personalComputers = personalComputers;
    }

    public long getLaptops() {
        return laptops;
    }

    public long getMonitors() {
        return monitors;
    }

    public long getHardDrives() {
        return hardDrives;
    }

    public long getPersonalComputers() {
        return personalComputers;
    }

    public long getTotal() {
        return laptops + monitors + hardDrives + personalComputers;
    }
}

