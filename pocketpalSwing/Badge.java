//Badge
// CO Mapping: CO4, CO5
public class Badge {
    private final String name;
    private final String desc;
    private double progress;
    private boolean unlocked;

    public Badge(String name, String desc) {
        this.name = name; this.desc = desc; this.progress = 0; this.unlocked = false;
    }
    public String getName(){ return name; }
    public String getDesc(){ return desc; }
    public double getProgress(){ return progress; }
    public void setProgress(double p){ this.progress = Math.max(0, Math.min(100,p)); if (this.progress>=100) unlocked=true; }
    public boolean isUnlocked(){ return unlocked; }
}
