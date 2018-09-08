package com.kairos.dto.user.staff;

/**
 * Created by oodles on 19/4/17.
 */
public class AvailableCountries {
    private String href;

    public String getHref ()
    {
        return href;
    }

    public void setHref (String href)
    {
        this.href = href;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [href = "+href+"]";
    }

}
