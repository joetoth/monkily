package com.monkily.interest.internal.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monkily.content.service.ContentService;
import com.monkily.interest.model.ContentView;
import com.monkily.interest.service.InterestService;
import com.monkily.utils.GUID;
import com.monkily.utils.StringUtils;

@Service
public class InterestServiceImpl implements InterestService {

	private final static Log log = LogFactory.getLog(InterestServiceImpl.class);
	
	@Autowired
	ContentService contentService;

	@PersistenceContext
	EntityManager entityManager;

	S3Service s3Service;

	public InterestServiceImpl() {
		final String AWSAccessKeyId = "n/a";
		final String SecretAccessKey = "n/a";

		AWSCredentials awsCredentials = new AWSCredentials(AWSAccessKeyId,
				SecretAccessKey);

		try {
			s3Service = new RestS3Service(awsCredentials);
		} catch (S3ServiceException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional
	public void processLogs() {

		log.debug("Processing Logs");
		
		try {
			S3Bucket bucket = s3Service.getBucket(BUCKET_LOGS_NAME);

			S3Object[] objects = s3Service.listObjects(bucket);

			for (S3Object object : objects) {

				String key = object.getKey();

				String split[] = key.substring(0, key.length() - 4).split("_");

				String userId = split[0];
				String ip = split[1];
				// String timeMillis = split[2];

				if (!userId.equals(UNKNOWN_USER_ID)) {

					object = s3Service.getObject(bucket, key);

					String log = StringUtils.convertStreamToString(object
							.getDataInputStream());

					String[][] data = StringUtils.parseTSV(log);

					Long lastDatetime = null;
					for (int i = 1; i < data.length; i++) {

						String url = data[i][0];
						Long datetime = new Long(data[i][1]);
						String contentType = data[i][2];

						boolean isText = isText(url, contentType);

						if (isText) {

							Long timeViewing = 0l;

							if (lastDatetime != null) {
								timeViewing = datetime - lastDatetime;
							}
							lastDatetime = datetime;

							if (url.toLowerCase().startsWith("http")) {

								String contentId = contentService
										.submitUrl(url);

								ContentView view = new ContentView();
								view.setId(GUID.generate());
								view.setContentId(contentId);
								view.setTimeRead(new Date(datetime));
								view.setUserId(userId);
								view.setTimeViewing(timeViewing);
								view.setIp(ip);

								saveView(view);
							}
						}
					}

				}

				// Copy to processed
				s3Service.moveObject(BUCKET_LOGS_NAME, key,
						BUCKET_LOGS_PROCESSED_NAME, new S3Object(key), false);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Transactional
	void saveView(ContentView view) {
		entityManager.persist(view);
	}

	@Override
	public List<ContentView> getViewsByUserId(String userId) {
		Query query = entityManager.createQuery("SELECT o FROM "
				+ ContentView.class.getName() + " o WHERE o.userId=:userId");
		query.setParameter("userId", userId);
		return query.getResultList();
	}

	private boolean isText(String url, String contentType) {

		boolean isText = true;

		url = url.toLowerCase();
		contentType = contentType.toLowerCase();

		if (url.endsWith(".js")
				|| url.endsWith(".gif")
				|| url.endsWith(".jpg")
				|| url.endsWith(".png")
				|| (!contentType.startsWith("text/html") && !contentType
						.startsWith("text/plain"))) {
			isText = false;
		}

		return isText;
	}

}
