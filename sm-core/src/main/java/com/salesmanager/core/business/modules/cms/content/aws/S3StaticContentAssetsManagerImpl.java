package com.salesmanager.core.business.modules.cms.content.aws;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
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
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.modules.cms.content.ContentAssetsManager;
import com.salesmanager.core.business.modules.cms.impl.CMSManager;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.content.OutputContentFile;

/**
 * Static content management with S3
 * 
 * @author carlsamson
 *
 */
public class S3StaticContentAssetsManagerImpl implements ContentAssetsManager {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(S3StaticContentAssetsManagerImpl.class);

	private static S3StaticContentAssetsManagerImpl fileManager = null;

	private CMSManager cmsManager;

	public static S3StaticContentAssetsManagerImpl getInstance() {

		if (fileManager == null) {
			fileManager = new S3StaticContentAssetsManagerImpl();
		}

		return fileManager;

	}

	@Override
	public OutputContentFile getFile(String merchantStoreCode, Optional<String> folderPath, FileContentType fileContentType, String contentName)
			throws ServiceException {
		try {
			// get buckets
			String bucketName = bucketName();

			final AmazonS3 s3 = s3Client();

			S3Object o = s3.getObject(bucketName, nodePath(merchantStoreCode, fileContentType) + contentName);

			LOGGER.info("Content getFile");
			return getOutputContentFile(IOUtils.toByteArray(o.getObjectContent()));
		} catch (final Exception e) {
			LOGGER.error("Error while getting file", e);
			throw new ServiceException(e);

		}
	}

	@Override
	public List<String> getFileNames(String merchantStoreCode, Optional<String> folderPath, FileContentType fileContentType)
			throws ServiceException {
		try {
			// get buckets
			String bucketName = bucketName();

			ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName)
					.withPrefix(nodePath(merchantStoreCode, fileContentType));

			List<String> fileNames = null;

			final AmazonS3 s3 = s3Client();
			ListObjectsV2Result results = s3.listObjectsV2(listObjectsRequest);
			List<S3ObjectSummary> objects = results.getObjectSummaries();
			for (S3ObjectSummary os : objects) {
				if (isInsideSubFolder(os.getKey())) {
					continue;
				}
				if (fileNames == null) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 20:46:27.040781):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `fileNames = new ArrayList<String>();` is most likely affected. - Reasoning: Instantiating a new `ArrayList` object inside the loop can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of the `ArrayList` object outside of the loop to avoid unnecessary memory allocation.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


					fileNames = new ArrayList<String>();
				}
				String mimetype = URLConnection.guessContentTypeFromName(os.getKey());
				if (!StringUtils.isBlank(mimetype)) {
					fileNames.add(getName(os.getKey()));
				}
			}

			LOGGER.info("Content get file names");
			return fileNames;
		} catch (final Exception e) {
			LOGGER.error("Error while getting file names", e);
			throw new ServiceException(e);

		}
	}

	@Override
	public List<OutputContentFile> getFiles(String merchantStoreCode, Optional<String> folderPath, FileContentType fileContentType)
			throws ServiceException {
		try {
			// get buckets
			String bucketName = bucketName();

			ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName)
					.withPrefix(nodePath(merchantStoreCode, fileContentType));

			List<OutputContentFile> files = null;
			final AmazonS3 s3 = s3Client();
			ListObjectsV2Result results = s3.listObjectsV2(listObjectsRequest);
			List<S3ObjectSummary> objects = results.getObjectSummaries();
			for (S3ObjectSummary os : objects) {
				if (files == null) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 20:46:27.040781):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<OutputContentFile> files = null;` is most likely affected. - Reasoning: The line instantiates a new `ArrayList` inside the loop, which is an inefficient use of memory. - Proposed solution: Move the instantiation of `files` outside the loop to avoid unnecessary memory allocation.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


					files = new ArrayList<OutputContentFile>();
				}
				String mimetype = URLConnection.guessContentTypeFromName(os.getKey());
				if (!StringUtils.isBlank(mimetype)) {
					S3Object o = s3.getObject(bucketName, os.getKey());
					byte[] byteArray = IOUtils.toByteArray(o.getObjectContent());
/**********************************
 * CAST-Finding START #3 (2024-02-01 20:46:27.040781):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `files = new ArrayList<OutputContentFile>();` is most likely affected. - Reasoning: It is inside the loop and creates a new ArrayList at each iteration, which is unnecessary and can be moved outside the loop. - Proposed solution: Move the line `files = new ArrayList<OutputContentFile>();` outside the loop to avoid unnecessary instantiation at each iteration.  The code line `ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);` is most likely affected. - Reasoning: It creates a new `ByteArrayOutputStream` at each iteration, which is unnecessary and can be moved outside the loop. - Proposed solution: Move the line `ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);` outside the loop to avoid unnecessary instantiation at each iteration.  The code line `baos.write(byteArray, 0, byteArray.length);` is most likely affected. - Reasoning: It is inside the loop and writes to the `ByteArrayOutputStream` at each iteration, which can be avoided by moving it outside the loop. - Proposed solution: Move the line `baos.write(byteArray, 0, byteArray.length);` outside the loop to avoid unnecessary writes to the `ByteArrayOutputStream` at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


/**********************************
 * CAST-Finding START #4 (2024-02-01 20:46:27.040781):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);` is most likely affected. - Reasoning: It instantiates a new `ByteArrayOutputStream` object inside the loop, which can be avoided by creating the object once outside the loop and reusing it. - Proposed solution: Move the instantiation of `ByteArrayOutputStream` outside the loop and reuse it at each iteration.  The code line `baos.write(byteArray, 0, byteArray.length);` is most likely affected. - Reasoning: It modifies the `baos` object inside the loop. If the object is created outside the loop, it can be reused and its value can be changed at each iteration. - Proposed solution: Move the instantiation of `ByteArrayOutputStream` outside the loop and reuse it at each iteration.  The code line `OutputContentFile ct = new OutputContentFile();` is most likely affected. - Reasoning: It instantiates a new `OutputContentFile` object inside the loop, which can be avoided by creating the object once outside the loop and reusing it. - Proposed solution: Move the instantiation of `OutputContentFile` outside the loop and reuse it at each iteration.  The code line `ct.setFile(baos);` is most likely affected. - Reasoning: It sets the `file` property of the `ct` object inside the loop. If the object is created outside the loop, it can be reused and its value can be changed at each iteration. - Proposed solution: Move the instantiation of `OutputContentFile` outside the loop and reuse it at each iteration.  The code line `files.add(ct);` is most likely affected. - Reasoning: It adds the `ct` object to the `files` list inside the loop. If the object is created outside the loop, it can be reused and its value can be changed at each iteration. - Proposed solution: Move the instantiation of `OutputContentFile` outside the loop and reuse it
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


					OutputContentFile ct = new OutputContentFile();
					ct.setFile(baos);
					files.add(ct);
				}
			}

			LOGGER.info("Content getFiles");
			return files;
		} catch (final Exception e) {
			LOGGER.error("Error while getting files", e);
			throw new ServiceException(e);

		}
	}

	@Override
	public void addFile(String merchantStoreCode, Optional<String> folderPath, InputContentFile inputStaticContentData) throws ServiceException {

		try {
			// get buckets
			String bucketName = bucketName();

			String nodePath = nodePath(merchantStoreCode, inputStaticContentData.getFileContentType());

			final AmazonS3 s3 = s3Client();

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(inputStaticContentData.getMimeType());
			PutObjectRequest request = new PutObjectRequest(bucketName, nodePath + inputStaticContentData.getFileName(),
					inputStaticContentData.getFile(), metadata);
			request.setCannedAcl(CannedAccessControlList.PublicRead);

			s3.putObject(request);

			LOGGER.info("Content add file");
		} catch (final Exception e) {
			LOGGER.error("Error while adding file", e);
			throw new ServiceException(e);

		}

	}

	@Override
	public void addFiles(String merchantStoreCode, Optional<String> folderPath, List<InputContentFile> inputStaticContentDataList)
			throws ServiceException {

		if (CollectionUtils.isNotEmpty(inputStaticContentDataList)) {
			for (InputContentFile inputFile : inputStaticContentDataList) {
				this.addFile(merchantStoreCode, folderPath, inputFile);
			}

		}

	}

	@Override
	public void removeFile(String merchantStoreCode, FileContentType staticContentType, String fileName, Optional<String> folderPath)
			throws ServiceException {

		try {
			// get buckets
			String bucketName = bucketName();

			final AmazonS3 s3 = s3Client();
			s3.deleteObject(bucketName, nodePath(merchantStoreCode, staticContentType) + fileName);

			LOGGER.info("Remove file");
		} catch (final Exception e) {
			LOGGER.error("Error while removing file", e);
			throw new ServiceException(e);

		}

	}

	@Override
	public void removeFiles(String merchantStoreCode, Optional<String> folderPath) throws ServiceException {

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
		String region = regionName();
		LOGGER.debug("AWS CMS Using region " + region);

		return AmazonS3ClientBuilder.standard().withRegion(region) // The
																			// first
																			// region
																			// to
																			// try
																			// your
																			// request
																			// against
				.build();
	}

	private String regionName() {
		String regionName = getCmsManager().getLocation();
		if (StringUtils.isBlank(regionName)) {
			regionName = DEFAULT_REGION_NAME;
		}
		return regionName;
	}

	public CMSManager getCmsManager() {
		return cmsManager;
	}

	public void setCmsManager(CMSManager cmsManager) {
		this.cmsManager = cmsManager;
	}

	@Override
	public void addFolder(String merchantStoreCode, String folderName, Optional<String> folderPath) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFolder(String merchantStoreCode, String folderName, Optional<String> folderPath) throws ServiceException {
		// TODO Auto-generated method stub

	}


	@Override
	public List<String> listFolders(String merchantStoreCode, Optional<String> path) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
