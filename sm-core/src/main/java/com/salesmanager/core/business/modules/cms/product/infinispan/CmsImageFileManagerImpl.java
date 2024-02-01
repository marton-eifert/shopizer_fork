package com.salesmanager.core.business.modules.cms.product.infinispan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.impl.CMSManager;
import com.salesmanager.core.business.modules.cms.impl.CacheManager;
import com.salesmanager.core.business.modules.cms.product.ProductAssetsManager;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.ProductImageSize;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.ImageContentFile;
import com.salesmanager.core.model.content.OutputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;

/**
 * Manager for storing in retrieving image files from the CMS This is a layer on top of Infinispan
 * https://docs.jboss.org/author/display/ISPN/Tree+API+Module
 * 
 * Manages - Product images
 * 
 * @author Carl Samson
 */
public class CmsImageFileManagerImpl implements ProductAssetsManager {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(CmsImageFileManagerImpl.class);

  private static CmsImageFileManagerImpl fileManager = null;

  private final static String ROOT_NAME = "product-merchant";

  private final static String SMALL = "SMALL";
  private final static String LARGE = "LARGE";

  private String rootName = ROOT_NAME;

  private CacheManager cacheManager;


  /**
   * Requires to stop the engine when image servlet un-deploys
   */
  public void stopFileManager() {

    try {
      LOGGER.info("Stopping CMS");
      cacheManager.getManager().stop();
    } catch (Exception e) {
      LOGGER.error("Error while stopping CmsImageFileManager", e);
    }
  }

  @PostConstruct
  void init() {

    this.rootName = cacheManager.getRootName();
    LOGGER.info("init " + getClass().getName() + " setting root" + this.rootName);

  }

  public static CmsImageFileManagerImpl getInstance() {

    if (fileManager == null) {
      fileManager = new CmsImageFileManagerImpl();
    }


    return fileManager;

  }

  private CmsImageFileManagerImpl() {

  }

  /**
   * root -productFiles -merchant-id PRODUCT-ID(key) -> CacheAttribute(value) - image 1 - image 2 -
   * image 3
   */

  @Override
  public void addProductImage(ProductImage productImage, ImageContentFile contentImage)
      throws ServiceException {

    if (cacheManager.getTreeCache() == null) {
      throw new ServiceException(
          "CmsImageFileManagerInfinispan has a null cacheManager.getTreeCache()");
    }

    try {

      // node
      StringBuilder nodePath = new StringBuilder();
      nodePath.append(productImage.getProduct().getMerchantStore().getCode())
          .append(Constants.SLASH).append(productImage.getProduct().getSku())
          .append(Constants.SLASH);


      if (contentImage.getFileContentType().name().equals(FileContentType.PRODUCT.name())) {
        nodePath.append(SMALL);
      } else if (contentImage.getFileContentType().name()
          .equals(FileContentType.PRODUCTLG.name())) {
        nodePath.append(LARGE);
      }

      Node<String, Object> productNode = this.getNode(nodePath.toString());


      InputStream isFile = contentImage.getFile();

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      IOUtils.copy(isFile, output);


      // object for a given product containing all images
      productNode.put(contentImage.getFileName(), output.toByteArray());



    } catch (Exception e) {

      throw new ServiceException(e);

    }

  }

  @Override
  public OutputContentFile getProductImage(ProductImage productImage) throws ServiceException {

    return getProductImage(productImage.getProduct().getMerchantStore().getCode(),
        productImage.getProduct().getSku(), productImage.getProductImage());

  }


  public List<OutputContentFile> getImages(MerchantStore store, FileContentType imageContentType)
      throws ServiceException {

    return getImages(store.getCode(), imageContentType);

  }

  @Override
  public List<OutputContentFile> getImages(Product product) throws ServiceException {

    if (cacheManager.getTreeCache() == null) {
      throw new ServiceException(
          "CmsImageFileManagerInfinispan has a null cacheManager.getTreeCache()");
    }

    List<OutputContentFile> images = new ArrayList<OutputContentFile>();


    try {


      FileNameMap fileNameMap = URLConnection.getFileNameMap();
      StringBuilder nodePath = new StringBuilder();
      nodePath.append(product.getMerchantStore().getCode());

      Node<String, Object> merchantNode = this.getNode(nodePath.toString());

      if (merchantNode == null) {
        return null;
      }


      for (String key : merchantNode.getKeys()) {

        byte[] imageBytes = (byte[]) merchantNode.get(key);





/**********************************
 * CAST-Finding START #1 (2024-02-01 20:52:40.641213):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


        OutputContentFile contentImage = new OutputContentFile();





/**********************************
 * CAST-Finding START #2 (2024-02-01 20:52:40.641213):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


        InputStream input = new ByteArrayInputStream(imageBytes);




/**********************************
 * CAST-Finding START #3 (2024-02-01 20:52:40.641213):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(input, output);

        String contentType = fileNameMap.getContentTypeFor(key);

        contentImage.setFile(output);
        contentImage.setMimeType(contentType);
        contentImage.setFileName(key);

        images.add(contentImage);


      }


    }

    catch (Exception e) {
      throw new ServiceException(e);
    } finally {

    }

    return images;
  }



  @SuppressWarnings("unchecked")
  @Override
  public void removeImages(final String merchantStoreCode) throws ServiceException {
    if (cacheManager.getTreeCache() == null) {
      throw new ServiceException(
          "CmsImageFileManagerInfinispan has a null cacheManager.getTreeCache()");
    }

    try {


      final StringBuilder merchantPath = new StringBuilder();
      merchantPath.append(getRootName()).append(merchantStoreCode);
      cacheManager.getTreeCache().getRoot().remove(merchantPath.toString());



    } catch (Exception e) {
      throw new ServiceException(e);
    } finally {

    }

  }


  @Override
  public void removeProductImage(ProductImage productImage) throws ServiceException {

    if (cacheManager.getTreeCache() == null) {
      throw new ServiceException(
          "CmsImageFileManagerInfinispan has a null cacheManager.getTreeCache()");
    }

    try {


      StringBuilder nodePath = new StringBuilder();
      nodePath.append(productImage.getProduct().getMerchantStore().getCode())
          .append(Constants.SLASH).append(productImage.getProduct().getSku());


      Node<String, Object> productNode = this.getNode(nodePath.toString());
      productNode.remove(productImage.getProductImage());



    } catch (Exception e) {
      throw new ServiceException(e);
    } finally {

    }

  }

  @Override
  public void removeProductImages(Product product) throws ServiceException {

    if (cacheManager.getTreeCache() == null) {
      throw new ServiceException(
          "CmsImageFileManagerInfinispan has a null cacheManager.getTreeCache()");
    }

    try {


      StringBuilder nodePath = new StringBuilder();
      nodePath.append(product.getMerchantStore().getCode());


      Node<String, Object> merchantNode = this.getNode(nodePath.toString());

      merchantNode.remove(product.getSku());



    } catch (Exception e) {
      throw new ServiceException(e);
    } finally {

    }

  }


  @Override
  public List<OutputContentFile> getImages(final String merchantStoreCode,
      FileContentType imageContentType) throws ServiceException {
    if (cacheManager.getTreeCache() == null) {
      throw new ServiceException(
          "CmsImageFileManagerInfinispan has a null cacheManager.getTreeCache()");
    }
    List<OutputContentFile> images = new ArrayList<OutputContentFile>();
    FileNameMap fileNameMap = URLConnection.getFileNameMap();

    try {


      StringBuilder nodePath = new StringBuilder();
      nodePath.append(merchantStoreCode);


      Node<String, Object> merchantNode = this.getNode(nodePath.toString());

      Set<Node<String, Object>> childs = merchantNode.getChildren();

      // TODO image sizes
      for (Node<String, Object> node : childs) {





/**********************************
 * CAST-Finding START #4 (2024-02-01 20:52:40.641213):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


        for (String key : node.getKeys()) {


          byte[] imageBytes = (byte[]) merchantNode.get(key);





/**********************************
 * CAST-Finding START #5 (2024-02-01 20:52:40.641213):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


          OutputContentFile contentImage = new OutputContentFile();





/**********************************
 * CAST-Finding START #6 (2024-02-01 20:52:40.641213):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


          InputStream input = new ByteArrayInputStream(imageBytes);




/**********************************
 * CAST-Finding START #7 (2024-02-01 20:52:40.641213):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


          ByteArrayOutputStream output = new ByteArrayOutputStream();
          IOUtils.copy(input, output);

          String contentType = fileNameMap.getContentTypeFor(key);

          contentImage.setFile(output);
          contentImage.setMimeType(contentType);
          contentImage.setFileName(key);

          images.add(contentImage);


        }

      }



    } catch (Exception e) {
      throw new ServiceException(e);
    } finally {

    }

    return images;
  }

  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName) throws ServiceException {
    return getProductImage(merchantStoreCode, productCode, imageName,
        ProductImageSize.SMALL.name());
  }

  @Override
  public OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName, ProductImageSize size) throws ServiceException {
    return getProductImage(merchantStoreCode, productCode, imageName, size.name());
  }

  private OutputContentFile getProductImage(String merchantStoreCode, String productCode,
      String imageName, String size) throws ServiceException {

    if (cacheManager.getTreeCache() == null) {
      throw new ServiceException(
          "CmsImageFileManagerInfinispan has a null cacheManager.getTreeCache()");
    }
    InputStream input = null;
    OutputContentFile contentImage = new OutputContentFile();
    try {

      FileNameMap fileNameMap = URLConnection.getFileNameMap();

      // SMALL by default
      StringBuilder nodePath = new StringBuilder();
      nodePath.append(merchantStoreCode).append(Constants.SLASH).append(productCode)
          .append(Constants.SLASH).append(size);

      Node<String, Object> productNode = this.getNode(nodePath.toString());


      byte[] imageBytes = (byte[]) productNode.get(imageName);

      if (imageBytes == null) {
        LOGGER.warn("Image " + imageName + " does not exist");
        return null;// no post processing will occur
      }

      input = new ByteArrayInputStream(imageBytes);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      IOUtils.copy(input, output);

      String contentType = fileNameMap.getContentTypeFor(imageName);

      contentImage.setFile(output);
      contentImage.setMimeType(contentType);
      contentImage.setFileName(imageName);



    } catch (Exception e) {
      throw new ServiceException(e);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (Exception ignore) {
        }
      }
    }

    return contentImage;

  }


  @SuppressWarnings("unchecked")
  private Node<String, Object> getNode(final String node) {
    LOGGER.debug("Fetching node for store {} from Infinispan", node);
    final StringBuilder merchantPath = new StringBuilder();
    merchantPath.append(getRootName()).append(node);

    Fqn contentFilesFqn = Fqn.fromString(merchantPath.toString());

    Node<String, Object> nd = cacheManager.getTreeCache().getRoot().getChild(contentFilesFqn);

    if (nd == null) {

      cacheManager.getTreeCache().getRoot().addChild(contentFilesFqn);
      nd = cacheManager.getTreeCache().getRoot().getChild(contentFilesFqn);

    }

    return nd;

  }

  public CacheManager getCacheManager() {
    return cacheManager;
  }

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public void setRootName(String rootName) {
    this.rootName = rootName;
  }

  public String getRootName() {
    return rootName;
  }



}
