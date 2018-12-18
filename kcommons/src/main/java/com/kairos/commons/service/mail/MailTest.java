package com.kairos.commons.service.mail;



import javax.inject.Inject;
import java.io.File;

/**
 * Created by oodles on 3/2/17.
 */
public class MailTest {
    @Inject
    private MailService mailService;

    public static void main(String[] args){
        MailService mailService = new MailService();
        File file = new File("/home/oodles/report.xlsx");

        try {
            if (mailService!=null){
                mailService.sendPlainMail("mohitramsharma@gmail.com","Hi","Report");
            }
        }
        catch (Exception e){
            System.out.print(e);
        }



    }
}
