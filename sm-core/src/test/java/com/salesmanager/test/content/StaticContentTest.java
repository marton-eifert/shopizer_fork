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
 * OUTLINE: The code line `throw new ServiceException( "Can't read" + file1.getAbsolutePath() );` is most likely affected. - Reasoning: The line throws an exception when the file cannot be read, indicating that the file is not being properly closed. - Proposed solution: Modify the code to explicitly close the file after throwing the exception to ensure proper resource management.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
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
 * OUTLINE: The code line `contentService.addContentFile(store.getCode(), cmsContentImage);` is most likely affected. - Reasoning: It adds an image file to the content service, which may consume system resources if not handled properly. - Proposed solution: Ensure that the file is closed after it is added to the content service.  The code line `OutputContentFile image = contentService.getContentFile(store.getCode(), FileContentType.IMAGE, file1.getName());` is most likely affected. - Reasoning: It retrieves an image file from the content service, which may consume system resources if not handled properly. - Proposed solution: Ensure that the file is closed after it is retrieved from the content service.  The code line `OutputStream outputStream = new FileOutputStream (OUTPUT_FOLDER + image.getFileName());` is most likely affected. - Reasoning: It opens an output stream to write the image file, which may consume system resources if not closed properly. - Proposed solution: Add explicit closing of the output stream using a try-with-resources block or by calling `outputStream.close()` after writing the image file.  The code line `ByteArrayOutputStream baos =  image.getFile();` is most likely affected. - Reasoning: It retrieves the image file as a byte array output stream, which may consume system resources if not handled properly. - Proposed solution: Add explicit closing of the byte array output stream using a try-with-resources block or by calling `baos.close()` after writing the image file.  The code line `baos.writeTo(outputStream);` is most likely affected. - Reasoning: It writes the image file to the output stream, which may consume system resources if not handled properly. - Proposed solution: Ensure that the output stream is properly closed after writing the image file.  The code line `contentService.removeFile(store.getCode(), FileContentType.IMAGE, file1.getName());` is most likely affected. - Reasoning: It removes the image file from the content service, which may consume system resources if not
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


   	 	OutputStream outputStream = new FileOutputStream (OUTPUT_FOLDER + image.getFileName()); 

   	 	ByteArrayOutputStream baos =  image.getFile();
   	 	baos.writeTo(outputStream);
		
		
		//remove image
   	 	contentService.removeFile(store.getCode(), FileContentType.IMAGE, file1.getName());
		


    }
	

}