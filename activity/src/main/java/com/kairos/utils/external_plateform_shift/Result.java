package com.kairos.utils.external_plateform_shift;


/**
 * Created by oodles on 22/1/17.
 */
public class Result {
    private String result;
    private String lines_output;
    private String lines_updated;
    private String nr_files_retrieved;
    private String entry_nr;
    private String is_stopped;
    private String lines_read;
    private String lines_rejected;
    private String lines_deleted;
    private String lines_written;
    private String log_channel_id;
    private Integer nr_errors;
    private String lines_input;
    private String exit_status;

    public String getResult ()
    {
        return result;
    }

    public void setResult (String result)
    {
        this.result = result;
    }

    public String getLines_output ()
    {
        return lines_output;
    }

    public void setLines_output (String lines_output)
    {
        this.lines_output = lines_output;
    }

    public String getLines_updated ()
    {
        return lines_updated;
    }

    public void setLines_updated (String lines_updated)
    {
        this.lines_updated = lines_updated;
    }

    public String getNr_files_retrieved ()
    {
        return nr_files_retrieved;
    }

    public void setNr_files_retrieved (String nr_files_retrieved)
    {
        this.nr_files_retrieved = nr_files_retrieved;
    }

    public String getEntry_nr ()
    {
        return entry_nr;
    }

    public void setEntry_nr (String entry_nr)
    {
        this.entry_nr = entry_nr;
    }





    public String getIs_stopped ()
    {
        return is_stopped;
    }

    public void setIs_stopped (String is_stopped)
    {
        this.is_stopped = is_stopped;
    }

    public String getLines_read ()
    {
        return lines_read;
    }

    public void setLines_read (String lines_read)
    {
        this.lines_read = lines_read;
    }

    public String getLines_rejected ()
    {
        return lines_rejected;
    }

    public void setLines_rejected (String lines_rejected)
    {
        this.lines_rejected = lines_rejected;
    }

    public String getLines_deleted ()
    {
        return lines_deleted;
    }

    public void setLines_deleted (String lines_deleted)
    {
        this.lines_deleted = lines_deleted;
    }

    public String getLines_written ()
    {
        return lines_written;
    }

    public void setLines_written (String lines_written)
    {
        this.lines_written = lines_written;
    }

    public String getLog_channel_id ()
    {
        return log_channel_id;
    }

    public void setLog_channel_id (String log_channel_id)
    {
        this.log_channel_id = log_channel_id;
    }

    public Integer getNr_errors ()
    {
        return nr_errors;
    }

    public void setNr_errors (Integer nr_errors)
    {
        this.nr_errors = nr_errors;
    }



    public String getLines_input ()
    {
        return lines_input;
    }

    public void setLines_input (String lines_input)
    {
        this.lines_input = lines_input;
    }

    public String getExit_status ()
    {
        return exit_status;
    }

    public void setExit_status (String exit_status)
    {
        this.exit_status = exit_status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [result = "+result+", lines_output = "+lines_output+", lines_updated = "+lines_updated+", nr_files_retrieved = "+nr_files_retrieved+", entry_nr = "+entry_nr+", is_stopped = "+is_stopped+", lines_read = "+lines_read+", lines_rejected = "+lines_rejected+", lines_deleted = "+lines_deleted+", lines_written = "+lines_written+", log_channel_id = "+log_channel_id+", nr_errors = "+nr_errors+", lines_input = "+lines_input+", exit_status = "+exit_status+"]";
    }
}
