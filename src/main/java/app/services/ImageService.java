package app.services;

import app.entities.Ticket;
import app.pojo.TicketImage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Service
public class ImageService implements app.services.Service
{

    private final Log logger = LogFactory.getLog( getClass() );

    private static String IMAGES_FOLDER = "C:/Users/Alexei/Desktop/dimopics/"; // TODO: 10/02/2016 move to .properties

    @Autowired
    private TicketService ticketService;

    //Using original file name as filename
    public void saveImage ( Long ticketId, MultipartFile image )
    {
        Ticket ticket = ticketService.getById( ticketId );
        //Check if image name already exists //// TODO: 30/7/2016 More sophisticated check (maybe hashing)
        if ( ticket.getImages().stream()
                .anyMatch( i -> i.getImageName().equalsIgnoreCase( image.getOriginalFilename() ) ) )
        {
            throw new IllegalArgumentException( "Image name " + image.getOriginalFilename()
                    + " already present for ticketId " + ticketId );
        }

        // TODO: 09/02/2016 : save location sto .properties
        File convFile = new File( IMAGES_FOLDER + image.getOriginalFilename() );
        try
        {
            image.transferTo( convFile );
            logger.info( "saved file to path : " + convFile.getAbsolutePath() );
            ticket.getImages().add( new TicketImage( image.getOriginalFilename() ) );
            ticketService.update( ticket );
        } catch ( IOException e )
        {
            logger.error( "Could not save file. Message : " + e.getMessage() );
            e.printStackTrace();
        } catch ( IllegalArgumentException e2 )
        {
            logger.error( "Illegal argument passed in findOne of TicketRepository" );
        }
    }
}
