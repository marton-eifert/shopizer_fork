package com.salesmanager.test.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.content.OutputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;



/**
 * Test 
 * 
 * - static content files (.js, .pdf etc)
 * - static content images (jpg, gig ...)
 * @author Carl Samson
 *
 */
@Ignore
public class StaticContentTest extends com.salesmanager.test.common.AbstractSalesManagerCoreTestCase {
	

	@Inject
	private ContentService contentService;
	
	/**
	 * Change this path to an existing image path
	 */
	private final static String IMAGE_FILE = "/Users/carlsamson/Documents/Database.png";
	
	private final static String OUTPUT_FOLDER = "/Users/carlsamson/Documents/test/";
	
	
    @Test
    public void createImage()
        throws ServiceException, FileNotFoundException, IOException
    {

        MerchantStore store = merchantService.getByCode( MerchantStore.DEFAULT_STORE );
        final File file1 = new File( IMAGE_FILE);

        if ( !file1.exists() || !file1.canRead() )
        {
            throw new ServiceException( "Can't read" + file1.getAbsolutePath() );
        }





/**********************************
 * CAST-Finding START #1 (2024-02-01 22:00:05.895404):
 * TITLE: Avoid Programs not using explicitly OPEN and CLOSE for files or streams
 * DESCRIPTION: Not closing files explicitly into your programs can occur memory issues. Leaving files opened unnecessarily has many downsides. They may consume limited system resources such as file descriptors. Code that deals with many such objects may exhaust those resources unnecessarily if they're not returned to the system promptly after use.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


        final byte[] is = IOUtils.toByteArray( new FileInputStream( file1 ) );
        final ByteArrayInputStream inputStream = new ByteArrayInputStream( is );
        final InputContentFile cmsContentImage = new InputContentFile();
        cmsContentImage.setFileName( file1.getName() );
        cmsContentImage.setFile( inputStream );
        cmsContentImage.setFileContentType(FileContentType.IMAGE);
        
        //Add image
        contentService.addContentFile(store.getCode(), cmsContentImage);

    
        //get image
		OutputContentFile image = contentService.getContentFile(store.getCode(), FileContentType.IMAGE, file1.getName());

        //print image




/**********************************
 * CAST-Finding START #2 (2024-02-01 22:00:05.895525):
 * TITLE: Avoid Programs not using explicitly OPEN and CLOSE for files or streams
 * DESCRIPTION: Not closing files explicitly into your programs can occur memory issues. Leaving files opened unnecessarily has many downsides. They may consume limited system resources such as file descriptors. Code that deals with many such objects may exhaust those resources unnecessarily if they're not returned to the system promptly after use.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


   	 	OutputStream outputStream = new FileOutputStream (OUTPUT_FOLDER + image.getFileName()); 

   	 	ByteArrayOutputStream baos =  image.getFile();
   	 	baos.writeTo(outputStream);
		
		
		//remove image
   	 	contentService.removeFile(store.getCode(), FileContentType.IMAGE, file1.getName());
		


    }
	

}