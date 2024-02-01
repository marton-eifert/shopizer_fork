package com.salesmanager.core.business.modules.cms.product.gcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.StorageOptions;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.impl.CMSManager;
import com.salesmanager.core.business.modules.cms.product.ProductAssetsManager;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.ProductImageSize;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.ImageContentFile;
import com.salesmanager.core.model.content.OutputContentFile;

@Component("gcpProductAssetsManager")
public class GCPProductContentFileManager implements ProductAssetsManager {
  
  @Autowired 
  private CMSManager gcpAssetsManager;
  
  private static String DEFAULT_BUCKET_NAME = "shopizer";
  
  private static final Logger LOGGER = LoggerFactory.getLogger(GCPProductContentFileManager.class);

  

  private final static String SMALL = "SMALL";
  private final static String LARGE = "LARGE";

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName) throws ServiceException {
    // TODO Auto-generated method stub
    
 
    
    return null;
  }

  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName, ProductImageSize size) throws ServiceException {
    InputStream inputStream = null;
    try {
      Storage storage = StorageOptions.getDefaultInstance().getService();
      
      String bucketName = bucketName();
      
      if(!this.bucketExists(storage, bucketName)) {
        return null;
      }

      Blob blob = storage.get(BlobId.of(bucketName, filePath(merchantStoreCode,productCode, size.name(), imageName)));

      ReadChannel reader = blob.reader();
      
      inputStream = Channels.newInputStream(reader);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOUtils.copy(inputStream, outputStream);
      OutputContentFile ct = new OutputContentFile();
      ct.setFile(outputStream);
      ct.setFileName(blob.getName());

      

      return ct;
    } catch (final Exception e) {
      LOGGER.error("Error while getting files", e);
      throw new ServiceException(e);
  
    } finally {
      if(inputStream!=null) {
        try {
          inputStream.close();
        } catch(Exception ignore) {}
      }
      
    }
  
  }

  @Override
  public OutputContentFile getProductImage(ProductImage productImage) throws ServiceException {

    return null;
    
  }

  @Override
  public List<OutputContentFile> getImages(Product product) throws ServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * List files
   */
  @Override
  public List<OutputContentFile> getImages(String merchantStoreCode,
      FileContentType imageContentType) throws ServiceException {
    
    InputStream inputStream = null;
    try {
      Storage storage = StorageOptions.getDefaultInstance().getService();
      
      String bucketName = bucketName();
      
      if(!this.bucketExists(storage, bucketName)) {
        return null;
      }
      
      Page<Blob> blobs =
          storage.list(
              bucketName, BlobListOption.currentDirectory(), BlobListOption.prefix(merchantStoreCode));

      List<OutputContentFile> files = new ArrayList<OutputContentFile>();
      for (Blob blob : blobs.iterateAll()) {
        blob.getName();
        ReadChannel reader = blob.reader();
        inputStream = Channels.newInputStream(reader);




/**********************************
 * CAST-Finding START #1 (2024-02-01 20:51:05.408249):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `blob.getName();` is most likely affected.  - Reasoning: Calling `getName()` at each iteration of the loop can result in unnecessary method calls and impact performance.  - Proposed solution: Move the line `blob.getName();` outside the loop and store the result in a variable if it is needed later in the loop.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);


/**********************************
 * CAST-Finding START #2 (2024-02-01 20:51:05.408249):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ByteArrayOutputStream outputStream = new ByteArrayOutputStream();` is most likely affected.  - Reasoning: It instantiates a new `ByteArrayOutputStream` object inside the loop, which can be avoided.  - Proposed solution: Move the instantiation of `ByteArrayOutputStream` outside the loop and reuse the same object.  The code line `IOUtils.copy(inputStream, outputStream);` is most likely affected.  - Reasoning: It performs an operation inside the loop that could be optimized.  - Proposed solution: Move the `IOUtils.copy` operation outside the loop if possible.  The code line `OutputContentFile ct = new OutputContentFile();` is most likely affected.  - Reasoning: It instantiates a new `OutputContentFile` object inside the loop, which can be avoided.  - Proposed solution: Move the instantiation of `OutputContentFile` outside the loop and reuse the same object.  The code line `ct.setFile(outputStream);` is most likely affected.  - Reasoning: It modifies the `file` property of the `OutputContentFile` object inside the loop, which can be optimized.  - Proposed solution: Modify the logic to set the `file` property of the `OutputContentFile` object outside the loop if possible.  The code line `files.add(ct);` is most likely affected.  - Reasoning: It adds the `OutputContentFile` object to the `files` list inside the loop, which can be optimized.  - Proposed solution: Add the `OutputContentFile` object to the `files` list outside the loop if possible.  The code line `return files;` is most likely affected.  - Reasoning: It returns the `files` list inside the loop, which can be optimized.  - Proposed solution: Return the `files` list outside the loop if possible.  The code line `} catch (final Exception e) {` is most likely affected.  - Reasoning: It catches an exception inside the loop, which can be optimized. 
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


        OutputContentFile ct = new OutputContentFile();
        ct.setFile(outputStream);
        files.add(ct);
      }

      return files;
    } catch (final Exception e) {
      LOGGER.error("Error while getting files", e);
      throw new ServiceException(e);
  
    } finally {
      if(inputStream!=null) {
        try {
          inputStream.close();
        } catch(Exception ignore) {}
      }
      
    }
  }

  @Override
  public void addProductImage(ProductImage productImage, ImageContentFile contentImage)
      throws ServiceException {
    
    Storage storage = StorageOptions.getDefaultInstance().getService();
    
    String bucketName = bucketName();

    if(!this.bucketExists(storage, bucketName)) {
      createBucket(storage, bucketName);
    }
    
    //build filename
    StringBuilder fileName = new StringBuilder()
        .append(filePath(productImage.getProduct().getMerchantStore().getCode(), productImage.getProduct().getSku(), contentImage.getFileContentType()))
        .append(productImage.getProductImage());
    
    
      try {
        byte[] targetArray = IOUtils.toByteArray(contentImage.getFile());
        BlobId blobId = BlobId.of(bucketName, fileName.toString());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
        storage.create(blobInfo, targetArray);
        Acl acl = storage.createAcl(blobId, Acl.of(User.ofAllUsers(), Role.READER));
      } catch (IOException ioe) {
        throw new ServiceException(ioe);
      }

    
  }

  @Override
  public void removeProductImage(ProductImage productImage) throws ServiceException {
    
    //delete all image sizes
    Storage storage = StorageOptions.getDefaultInstance().getService();

    List<String> sizes = Arrays.asList(SMALL, LARGE);
    for(String size : sizes) {
      String filePath = filePath(productImage.getProduct().getMerchantStore().getCode(), productImage.getProduct().getSku(), size, productImage.getProductImage());
      BlobId blobId = BlobId.of(bucketName(), filePath);
      if(blobId==null) {
/**********************************
 * CAST-Finding START #3 (2024-02-01 20:51:05.408249):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `for(String size : sizes) {` is most likely affected. - Reasoning: This line is the start of the loop where string concatenation may occur. - Proposed solution: Modify the line to use a `StringBuilder` or `StringJoiner` to avoid string concatenation inside the loop.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


        LOGGER.info("Image path " + filePath + " does not exist");
        return;
        //throw new ServiceException("Image not found " + productImage.getProductImage());
      }
/**********************************
 * CAST-Finding START #4 (2024-02-01 20:51:05.408249):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `LOGGER.info("Image path " + filePath + " does not exist");` is most likely affected. - Reasoning: It performs string concatenation inside a loop, which can result in quadratic running time and unnecessary temporary objects. - Proposed solution: Instead of concatenating the strings inside the `LOGGER.info()` method, it is recommended to use a `StringBuilder` or `String.format()` to improve performance and avoid unnecessary temporary objects. For example: `LOGGER.info("Image path {} does not exist", filePath);`  The code line `LOGGER.error("Cannot delete image [" + productImage.getProductImage() + "]");` is most likely affected. - Reasoning: It performs string concatenation. - Proposed solution: Use a `StringBuilder` or `String.format()` to improve performance and avoid unnecessary temporary objects. For example: `LOGGER.error("Cannot delete image [{}]", productImage.getProductImage());`
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #4
 **********************************/
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


        LOGGER.error("Cannot delete image [" + productImage.getProductImage() + "]");
      }
    }
  
  }

  @Override
  public void removeProductImages(Product product) throws ServiceException {

    
    Storage storage = StorageOptions.getDefaultInstance().getService();
    
    String bucketName = bucketName();

    Page<Blob> blobs =
        storage.list(
            bucketName, BlobListOption.currentDirectory(), BlobListOption.prefix(product.getSku()));

    
    for (Blob blob : blobs.iterateAll()) {
      // do something with the blob
      storage.delete(blob.getBlobId());
    }
    

  }

  @Override
  public void removeImages(String merchantStoreCode) throws ServiceException {
    Storage storage = StorageOptions.getDefaultInstance().getService();
    
    String bucketName = bucketName();

    Page<Blob> blobs =
        storage.list(
            bucketName, BlobListOption.currentDirectory(), BlobListOption.prefix(merchantStoreCode));

    
    for (Blob blob : blobs.iterateAll()) {
      // do something with the blob
      storage.delete(blob.getBlobId());
    }

  }
  
  private String bucketName() {
    String bucketName = gcpAssetsManager.getRootName();
    if (StringUtils.isBlank(bucketName)) {
      bucketName = DEFAULT_BUCKET_NAME;
    }
    return bucketName;
  }
  
  private boolean bucketExists(Storage storage, String bucketName) {
    Bucket bucket = storage.get(bucketName, BucketGetOption.fields(BucketField.NAME));
    if (bucket == null || !bucket.exists()) {
      return false;
    }
    return true;
  }
  
  private Bucket createBucket(Storage storage, String bucketName) {
    return storage.create(BucketInfo.of(bucketName));
  }
  
  private String filePath(String merchant, String sku, FileContentType contentImage) {
      StringBuilder sb = new StringBuilder();
      sb.append("products").append(Constants.SLASH);
      sb.append(merchant)
      .append(Constants.SLASH).append(sku).append(Constants.SLASH);

      // small large
      if (contentImage.name().equals(FileContentType.PRODUCT.name())) {
        sb.append(SMALL);
      } else if (contentImage.name().equals(FileContentType.PRODUCTLG.name())) {
        sb.append(LARGE);
      }

      return sb.append(Constants.SLASH).toString();
    
  }
  
  private String filePath(String merchant, String sku, String size, String fileName) {
    StringBuilder sb = new StringBuilder();
    sb.append("products").append(Constants.SLASH);
    sb.append(merchant)
    .append(Constants.SLASH).append(sku).append(Constants.SLASH);
    
    sb.append(size);
    sb.append(Constants.SLASH).append(fileName);

    return sb.toString();
  
  }


}