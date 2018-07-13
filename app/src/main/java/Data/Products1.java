package Data;

public class Products1 {

    public String name;

private double massa;
private double OH;
    public Products1(String name, double massa, double OH) {
        this.name=name;
        this.massa=massa;
        this.OH=OH;
    }

    public Products1() {

    }
    public double getmassa() {
        return massa;
    }

    public void setmassa(int massa) {
        this.massa=massa;
    }

    public String getName() {
        return name;
    }
    public double getOH() {
        return OH;
    }

    public void setName(String name) {
        this.name=name;
    }


}