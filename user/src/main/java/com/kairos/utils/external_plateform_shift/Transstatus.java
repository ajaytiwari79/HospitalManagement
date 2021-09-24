package com.kairos.utils.external_plateform_shift;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by oodles on 22/1/17.
 */
@XmlRootElement(name = "transstatus")
public class Transstatus {
    private String id;
    private String last_log_line_nr;
    private Result result;
    private String transname;
    private String logging_string;
    private String status_desc;
    private String paused;
    private String log_date;
    private String first_log_line_nr;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }


    public String getLast_log_line_nr ()
    {
        return last_log_line_nr;
    }

    public void setLast_log_line_nr (String last_log_line_nr)
    {
        this.last_log_line_nr = last_log_line_nr;
    }

    public Result getResult ()
    {
        return result;
    }

    public void setResult (Result result)
    {
        this.result = result;
    }

    public String getTransname ()
    {
        return transname;
    }

    public void setTransname (String transname)
    {
        this.transname = transname;
    }

    public String getLogging_string ()
    {
        return logging_string;
    }

    public void setLogging_string (String logging_string)
    {
        this.logging_string = logging_string;
    }

    public String getStatus_desc ()
    {
        return status_desc;
    }

    public void setStatus_desc (String status_desc)
    {
        this.status_desc = status_desc;
    }

    public String getPaused ()
    {
        return paused;
    }

    public void setPaused (String paused)
    {
        this.paused = paused;
    }

    public String getLog_date ()
    {
        return log_date;
    }

    public void setLog_date (String log_date)
    {
        this.log_date = log_date;
    }

    public String getFirst_log_line_nr ()
    {
        return first_log_line_nr;
    }

    public void setFirst_log_line_nr (String first_log_line_nr)
    {
        this.first_log_line_nr = first_log_line_nr;
    }



    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", last_log_line_nr = "+last_log_line_nr+", result = "+result+", transname = "+transname+", logging_string = "+logging_string+", status_desc = "+status_desc+", paused = "+paused+", log_date = "+log_date+", first_log_line_nr = "+first_log_line_nr+"]";
    }
}
