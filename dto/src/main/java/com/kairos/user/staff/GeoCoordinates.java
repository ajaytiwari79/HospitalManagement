package com.kairos.user.staff;

/**
 * Created by oodles on 19/4/17.
 */
public class GeoCoordinates {
    private String present;

    private String y;

    private String x;

    public String getPresent ()
    {
        return present;
    }

    public void setPresent (String present)
    {
        this.present = present;
    }

    public String getY ()
    {
        return y;
    }

    public void setY (String y)
    {
        this.y = y;
    }

    public String getX ()
    {
        return x;
    }

    public void setX (String x)
    {
        this.x = x;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [present = "+present+", y = "+y+", x = "+x+"]";
    }
}
