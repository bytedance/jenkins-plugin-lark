package plugin;

import com.alibaba.fastjson.JSONObject;
import hudson.ProxyConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class LarkServiceImpl implements LarkService {

    private Logger logger = LoggerFactory.getLogger(LarkService.class);

    private boolean onStart;

    private boolean onSuccess;

    private boolean onFailed;

    private boolean onAbort;

    private TaskListener listener;

    private AbstractBuild build;

    private String[] urlList;


    private static final String START = "start";
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";
    private static final String ABORT = "abort";
    private static final String TYPE = "job";


    public LarkServiceImpl(String sendUrlList, boolean onStart, boolean onSuccess, boolean onFailed, boolean onAbort, TaskListener listener, AbstractBuild build) {
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.onAbort = onAbort;
        this.listener = listener;
        this.build = build;
        urlList = parseSendList(sendUrlList);
    }

    @Override
    public void start(String userDescription) {
        if (onStart) {
            logger.info("send link msg from " + listener.toString());
            sendMsg(START, userDescription);
        }

    }

    private String[] parseSendList(String sendList) {
        String[] urlLis = null;
        try {
            urlLis = sendList.split(";");
        } catch (Exception e) {
            logger.error("parse list error", e);
        }
        return urlLis;
    }

    private String getBuildUrl() {
        String getRootUrl = getDefaultURL();
        if (getRootUrl.endsWith("/")) {
            return getRootUrl + build.getUrl();
        } else {
            return getRootUrl + "/" + build.getUrl();
        }
    }

    private String getJobUrl() {
        String getRootUrl = getDefaultURL();
        if (getRootUrl.endsWith("/")) {
            return getRootUrl + build.getProject().getUrl();
        } else {
            return getRootUrl + "/" + build.getProject().getUrl();
        }
    }

    private String getDefaultURL() {
        Jenkins instance = Jenkins.getInstance();
        return instance.getRootUrl() != null ? instance.getRootUrl() : "";
    }

    @Override
    public void success(String userDescription) {
        logger.info("description:" + userDescription);
        if (onSuccess) {
            logger.info("send link msg from " + listener.toString());
            sendMsg(SUCCESS, userDescription);
        }
    }

    @Override
    public void failed() {
        if (onFailed) {
            logger.info("send link msg from " + listener.toString());
            sendMsg(FAILED);
        }
    }

    @Override
    public void abort() {
        if (onAbort) {
            logger.info("send link msg from " + listener.toString());
            sendMsg(ABORT);
        }
    }

    private void sendMsg(String type) {
        sendMsg(type, null);
    }

    private void sendMsg(String type, @Nullable String shortDescription) {
        if (urlList == null) {
            return;
        }
        HttpClient client = getHttpClient();
        System.out.println(urlList.toString());
        JSONObject body = new JSONObject();
        body.put("action", type);
        body.put("type", TYPE);
        body.put("name", build.getProject().getDisplayName());
        body.put("order", build.getDisplayName());
        body.put("buildurl", getBuildUrl());
        body.put("joburl", getJobUrl());
        body.put("duration", build.getDuration());

        if ((StringUtils.equals(type, START) || StringUtils.equals(type, SUCCESS)) && shortDescription != null) {
            body.put("causeby", shortDescription);
        }
        String bodyJsonString = body.toJSONString();
        logger.info(bodyJsonString);

        StringRequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(bodyJsonString, "application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("requestEntity encode error", e);
        }

        for (String url : urlList) {
            if (!TextUtils.isEmpty(url)) {
                PostMethod post = new PostMethod(url);
                post.setRequestEntity(requestEntity);

                try {
                    client.executeMethod(post);
                    logger.info(post.getResponseBodyAsString());
                } catch (IOException e) {
                    logger.error("send msg error", e);
                } finally {
                    post.releaseConnection();
                }
            }
        }
    }

    private HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null && jenkins.proxy != null) {
            ProxyConfiguration proxy = jenkins.proxy;
            if (proxy != null && client.getHostConfiguration() != null) {
                client.getHostConfiguration().setProxy(proxy.name, proxy.port);
                String username = proxy.getUserName();
                String password = proxy.getPassword();
                if (username != null && !"".equals(username.trim())) {
                    logger.info("Using proxy authentication (user=" + username + ")");
                    client.getState().setProxyCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(username, password));
                }
            }
        }
        return client;
    }
}
