package com.salesmanager.core.business.modules.cms.product.aws;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
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

/**
 * Product content file manager with AWS S3
 * 
 * @author carlsamson
 *
 */
public class S3ProductContentFileManager
    implements ProductAssetsManager {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;



  private static final Logger LOGGER = LoggerFactory.getLogger(S3ProductContentFileManager.class);



  private static S3ProductContentFileManager fileManager = null;

  private static String DEFAULT_BUCKET_NAME = "shopizer-content";
  private static String DEFAULT_REGION_NAME = "us-east-1";
  private static final String ROOT_NAME = "products";

  private static final char UNIX_SEPARATOR = '/';
  private static final char WINDOWS_SEPARATOR = '\\';


  private final static String SMALL = "SMALL";
  private final static String LARGE = "LARGE";

  private CMSManager cmsManager;

  public static S3ProductContentFileManager getInstance() {

    if (fileManager == null) {
      fileManager = new S3ProductContentFileManager();
    }

    return fileManager;

  }

  @Override
  public List<OutputContentFile> getImages(String merchantStoreCode,
      FileContentType imageContentType) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();



      ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
          .withBucketName(bucketName).withPrefix(nodePath(merchantStoreCode));

      List<OutputContentFile> files = null;
      final AmazonS3 s3 = s3Client();
      ListObjectsV2Result results = s3.listObjectsV2(listObjectsRequest);
      List<S3ObjectSummary> objects = results.getObjectSummaries();
      for (S3ObjectSummary os : objects) {
        if (files == null) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 20:50:03.476086):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<S3ObjectSummary> objects = results.getObjectSummaries();` is most likely affected. - Reasoning: The code retrieves the object summaries from the listObjectsResult at each iteration of the loop, which can be optimized by storing them in a local variable outside the loop. - Proposed solution: Move the line `List<S3ObjectSummary> objects = results.getObjectSummaries();` outside the loop to store the object summaries in a local variable before the loop.  The code line `files = new ArrayList<OutputContentFile>();` is most likely affected. - Reasoning: The code instantiates a new ArrayList at each iteration of the loop, which can be optimized by moving the instantiation outside the loop. - Proposed solution: Move the line `files = new ArrayList<OutputContentFile>();` outside the loop to instantiate the ArrayList only once.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


          files = new ArrayList<OutputContentFile>();
        }
        String mimetype = URLConnection.guessContentTypeFromName(os.getKey());
        if (!StringUtils.isBlank(mimetype)) {
          S3Object o = s3.getObject(bucketName, os.getKey());
          byte[] byteArray = IOUtils.toByteArray(o.getObjectContent());


/**********************************
 * CAST-Finding START #2 (2024-02-01 20:50:03.476086):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `files = new ArrayList<OutputContentFile>();` is most likely affected. - Reasoning: It is inside the loop and creates a new ArrayList at each iteration, which is unnecessary and can be moved outside the loop. - Proposed solution: Move the line `files = new ArrayList<OutputContentFile>();` outside the loop to avoid unnecessary instantiation at each iteration.  The code line `ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);` is most likely affected. - Reasoning: It creates a new `ByteArrayOutputStream` at each iteration, which is unnecessary and can be moved outside the loop. - Proposed solution: Move the line `ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);` outside the loop to avoid unnecessary instantiation at each iteration.  The code line `baos.write(byteArray, 0, byteArray.length);` is most likely affected. - Reasoning: It is inside the loop and writes to the `ByteArrayOutputStream` at each iteration, which can be avoided by moving it outside the loop. - Proposed solution: Move the line `baos.write(byteArray, 0, byteArray.length);` outside the loop to avoid unnecessary writes to the `ByteArrayOutputStream` at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


          ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);
          baos.write(byteArray, 0, byteArray.length);
/**********************************
 * CAST-Finding START #3 (2024-02-01 20:50:03.476086):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);` is most likely affected.  - Reasoning: Instantiating a new `ByteArrayOutputStream` object inside a loop can be a performance issue as object instantiation is a greedy operation.  - Proposed solution: Move the instantiation of `ByteArrayOutputStream` outside the loop and reuse the same object at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


          OutputContentFile ct = new OutputContentFile();
          ct.setFile(baos);
          files.add(ct);
        }
      }

      return files;
    } catch (final Exception e) {
      LOGGER.error("Error while getting files", e);
      throw new ServiceException(e);

    }
  }

  @Override
  public void removeImages(String merchantStoreCode) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();

      final AmazonS3 s3 = s3Client();
      s3.deleteObject(bucketName, nodePath(merchantStoreCode));

      LOGGER.info("Remove folder");
    } catch (final Exception e) {
      LOGGER.error("Error while removing folder", e);
      throw new ServiceException(e);

    }

  }

  @Override
  public void removeProductImage(ProductImage productImage) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();

      final AmazonS3 s3 = s3Client();
      s3.deleteObject(bucketName, nodePath(productImage.getProduct().getMerchantStore().getCode(),
          productImage.getProduct().getSku()) + productImage.getProductImage());

      LOGGER.info("Remove file");
    } catch (final Exception e) {
      LOGGER.error("Error while removing file", e);
      throw new ServiceException(e);

    }

  }

  @Override
  public void removeProductImages(Product product) throws ServiceException {
    try {
      // get buckets
      String bucketName = bucketName();

      final AmazonS3 s3 = s3Client();
      s3.deleteObject(bucketName, nodePath(product.getMerchantStore().getCode(), product.getSku()));

      LOGGER.info("Remove file");
    } catch (final Exception e) {
      LOGGER.error("Error while removing file", e);
      throw new ServiceException(e);

    }

  }

  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName) throws ServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName, ProductImageSize size) throws ServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OutputContentFile getProductImage(ProductImage productImage) throws ServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<OutputContentFile> getImages(Product product) throws ServiceException {
    return null;
  }

  @Override
  public void addProductImage(ProductImage productImage, ImageContentFile contentImage)
      throws ServiceException {


    try {
      // get buckets
      String bucketName = bucketName();
      final AmazonS3 s3 = s3Client();

      String nodePath = this.nodePath(productImage.getProduct().getMerchantStore().getCode(),
          productImage.getProduct().getSku(), contentImage);


      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType(contentImage.getMimeType());

      PutObjectRequest request = new PutObjectRequest(bucketName,
          nodePath + productImage.getProductImage(), contentImage.getFile(), metadata);
      request.setCannedAcl(CannedAccessControlList.PublicRead);


      s3.putObject(request);


      LOGGER.info("Product add file");

    } catch (final Exception e) {
      LOGGER.error("Error while removing file", e);
      throw new ServiceException(e);

    }


  }


  private Bucket getBucket(String bucket_name) {
    final AmazonS3 s3 = s3Client();
    Bucket named_bucket = null;
    List<Bucket> buckets = s3.listBuckets();
    for (Bucket b : buckets) {
      if (b.getName().equals(bucket_name)) {
        named_bucket = b;
      }
    }

    if (named_bucket == null) {
      named_bucket = createBucket(bucket_name);
    }

    return named_bucket;
  }

  private Bucket createBucket(String bucket_name) {
    final AmazonS3 s3 = s3Client();
    Bucket b = null;
    if (s3.doesBucketExistV2(bucket_name)) {
      System.out.format("Bucket %s already exists.\n", bucket_name);
      b = getBucket(bucket_name);
    } else {
      try {
        b = s3.createBucket(bucket_name);
      } catch (AmazonS3Exception e) {
        System.err.println(e.getErrorMessage());
      }
    }
    return b;
  }

  /**
   * Builds an amazon S3 client
   * 
   * @return
   */
  private AmazonS3 s3Client() {

    return AmazonS3ClientBuilder.standard().withRegion(regionName()) // The first region to
                                                                            // try your request
                                                                            // against
        .build();
  }

  private String bucketName() {
    String bucketName = getCmsManager().getRootName();
    if (StringUtils.isBlank(bucketName)) {
      bucketName = DEFAULT_BUCKET_NAME;
    }
    return bucketName;
  }

  private String regionName() {
    String regionName = getCmsManager().getLocation();
    if (StringUtils.isBlank(regionName)) {
      regionName = DEFAULT_REGION_NAME;
    }
    return regionName;
  }

  private String nodePath(String store) {
    return new StringBuilder().append(ROOT_NAME).append(Constants.SLASH).append(store)
        .append(Constants.SLASH).toString();
  }

  private String nodePath(String store, String product) {

    StringBuilder sb = new StringBuilder();
    // node path
    String nodePath = nodePath(store);
    sb.append(nodePath);

    // product path
    sb.append(product).append(Constants.SLASH);
    return sb.toString();

  }

  private String nodePath(String store, String product, ImageContentFile contentImage) {

    StringBuilder sb = new StringBuilder();
    // node path
    String nodePath = nodePath(store, product);
    sb.append(nodePath);

    // small large
    if (contentImage.getFileContentType().name().equals(FileContentType.PRODUCT.name())) {
      sb.append(SMALL);
    } else if (contentImage.getFileContentType().name().equals(FileContentType.PRODUCTLG.name())) {
      sb.append(LARGE);
    }

    return sb.append(Constants.SLASH).toString();


  }

  public static String getName(String filename) {
    if (filename == null) {
      return null;
    }
    int index = indexOfLastSeparator(filename);
    return filename.substring(index + 1);
  }

  public static int indexOfLastSeparator(String filename) {
    if (filename == null) {
      return -1;
    }
    int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
    int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
    return Math.max(lastUnixPos, lastWindowsPos);
  }



  public CMSManager getCmsManager() {
    return cmsManager;
  }

  public void setCmsManager(CMSManager cmsManager) {
    this.cmsManager = cmsManager;
  }


}
