/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-10-30 15:57:41 UTC)
 * on 2013-11-07 at 20:21:30 UTC 
 * Modify at your own risk.
 */

package com.appspot.hazard_alert.alertendpoint;

/**
 * Service definition for Alertendpoint (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link AlertendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Alertendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.16.0-rc of the alertendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://hazard-alert.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "alertendpoint/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Alertendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Alertendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * An accessor for creating requests from the Alert collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Alertendpoint alertendpoint = new Alertendpoint(...);}
   *   {@code Alertendpoint.Alert.List request = alertendpoint.alert().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public Alert alert() {
    return new Alert();
  }

  /**
   * The "alert" collection of methods.
   */
  public class Alert {

    /**
     * Create a request for the method "alert.find".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link Find#execute()} method to invoke the remote operation.
     *
     * @return the request
     */
    public Find find() throws java.io.IOException {
      Find result = new Find();
      initialize(result);
      return result;
    }

    public class Find extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.AlertTransport> {

      private static final String REST_PATH = "alertFind";

      /**
       * Create a request for the method "alert.find".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link Find#execute()} method to invoke the remote operation. <p>
       * {@link Find#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
       * must be called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @since 1.13
       */
      protected Find() {
        super(Alertendpoint.this, "POST", REST_PATH, null, com.appspot.hazard_alert.alertendpoint.model.AlertTransport.class);
      }

      @Override
      public Find setAlt(java.lang.String alt) {
        return (Find) super.setAlt(alt);
      }

      @Override
      public Find setFields(java.lang.String fields) {
        return (Find) super.setFields(fields);
      }

      @Override
      public Find setKey(java.lang.String key) {
        return (Find) super.setKey(key);
      }

      @Override
      public Find setOauthToken(java.lang.String oauthToken) {
        return (Find) super.setOauthToken(oauthToken);
      }

      @Override
      public Find setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (Find) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public Find setQuotaUser(java.lang.String quotaUser) {
        return (Find) super.setQuotaUser(quotaUser);
      }

      @Override
      public Find setUserIp(java.lang.String userIp) {
        return (Find) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.String fullName;

      /**

       */
      public java.lang.String getFullName() {
        return fullName;
      }

      public Find setFullName(java.lang.String fullName) {
        this.fullName = fullName;
        return this;
      }

      @Override
      public Find set(String parameterName, Object value) {
        return (Find) super.set(parameterName, value);
      }
    }
    /**
     * Create a request for the method "alert.get".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link Get#execute()} method to invoke the remote operation.
     *
     * @param id
     * @return the request
     */
    public Get get(java.lang.Long id) throws java.io.IOException {
      Get result = new Get(id);
      initialize(result);
      return result;
    }

    public class Get extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.AlertTransport> {

      private static final String REST_PATH = "alert/{id}";

      /**
       * Create a request for the method "alert.get".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link Get#execute()} method to invoke the remote operation. <p>
       * {@link Get#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
       * must be called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @param id
       * @since 1.13
       */
      protected Get(java.lang.Long id) {
        super(Alertendpoint.this, "GET", REST_PATH, null, com.appspot.hazard_alert.alertendpoint.model.AlertTransport.class);
        this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
      }

      @Override
      public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
        return super.executeUsingHead();
      }

      @Override
      public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
        return super.buildHttpRequestUsingHead();
      }

      @Override
      public Get setAlt(java.lang.String alt) {
        return (Get) super.setAlt(alt);
      }

      @Override
      public Get setFields(java.lang.String fields) {
        return (Get) super.setFields(fields);
      }

      @Override
      public Get setKey(java.lang.String key) {
        return (Get) super.setKey(key);
      }

      @Override
      public Get setOauthToken(java.lang.String oauthToken) {
        return (Get) super.setOauthToken(oauthToken);
      }

      @Override
      public Get setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (Get) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public Get setQuotaUser(java.lang.String quotaUser) {
        return (Get) super.setQuotaUser(quotaUser);
      }

      @Override
      public Get setUserIp(java.lang.String userIp) {
        return (Get) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.Long id;

      /**

       */
      public java.lang.Long getId() {
        return id;
      }

      public Get setId(java.lang.Long id) {
        this.id = id;
        return this;
      }

      @Override
      public Get set(String parameterName, Object value) {
        return (Get) super.set(parameterName, value);
      }
    }
    /**
     * Create a request for the method "alert.list".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link List#execute()} method to invoke the remote operation.
     *
     * @param content the {@link com.appspot.hazard_alert.alertendpoint.model.AlertFilter}
     * @return the request
     */
    public List list(com.appspot.hazard_alert.alertendpoint.model.AlertFilter content) throws java.io.IOException {
      List result = new List(content);
      initialize(result);
      return result;
    }

    public class List extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.AlertTransportCollection> {

      private static final String REST_PATH = "alert";

      /**
       * Create a request for the method "alert.list".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link List#execute()} method to invoke the remote operation. <p>
       * {@link List#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
       * must be called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @param content the {@link com.appspot.hazard_alert.alertendpoint.model.AlertFilter}
       * @since 1.13
       */
      protected List(com.appspot.hazard_alert.alertendpoint.model.AlertFilter content) {
        super(Alertendpoint.this, "POST", REST_PATH, content, com.appspot.hazard_alert.alertendpoint.model.AlertTransportCollection.class);
      }

      @Override
      public List setAlt(java.lang.String alt) {
        return (List) super.setAlt(alt);
      }

      @Override
      public List setFields(java.lang.String fields) {
        return (List) super.setFields(fields);
      }

      @Override
      public List setKey(java.lang.String key) {
        return (List) super.setKey(key);
      }

      @Override
      public List setOauthToken(java.lang.String oauthToken) {
        return (List) super.setOauthToken(oauthToken);
      }

      @Override
      public List setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (List) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public List setQuotaUser(java.lang.String quotaUser) {
        return (List) super.setQuotaUser(quotaUser);
      }

      @Override
      public List setUserIp(java.lang.String userIp) {
        return (List) super.setUserIp(userIp);
      }

      @Override
      public List set(String parameterName, Object value) {
        return (List) super.set(parameterName, value);
      }
    }
    /**
     * Create a request for the method "alert.updateSubscription".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link UpdateSubscription#execute()} method to invoke the remote
     * operation.
     *
     * @param id
     * @param gcm
     * @param content the {@link com.appspot.hazard_alert.alertendpoint.model.Bounds}
     * @return the request
     */
    public UpdateSubscription updateSubscription(java.lang.Long id, java.lang.String gcm, com.appspot.hazard_alert.alertendpoint.model.Bounds content) throws java.io.IOException {
      UpdateSubscription result = new UpdateSubscription(id, gcm, content);
      initialize(result);
      return result;
    }

    public class UpdateSubscription extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.AlertTransportCollection> {

      private static final String REST_PATH = "alertcollection/{id}/{gcm}";

      /**
       * Create a request for the method "alert.updateSubscription".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link UpdateSubscription#execute()} method to invoke the remote
       * operation. <p> {@link UpdateSubscription#initialize(com.google.api.client.googleapis.services.A
       * bstractGoogleClientRequest)} must be called to initialize this instance immediately after
       * invoking the constructor. </p>
       *
       * @param id
       * @param gcm
       * @param content the {@link com.appspot.hazard_alert.alertendpoint.model.Bounds}
       * @since 1.13
       */
      protected UpdateSubscription(java.lang.Long id, java.lang.String gcm, com.appspot.hazard_alert.alertendpoint.model.Bounds content) {
        super(Alertendpoint.this, "PUT", REST_PATH, content, com.appspot.hazard_alert.alertendpoint.model.AlertTransportCollection.class);
        this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
        this.gcm = com.google.api.client.util.Preconditions.checkNotNull(gcm, "Required parameter gcm must be specified.");
      }

      @Override
      public UpdateSubscription setAlt(java.lang.String alt) {
        return (UpdateSubscription) super.setAlt(alt);
      }

      @Override
      public UpdateSubscription setFields(java.lang.String fields) {
        return (UpdateSubscription) super.setFields(fields);
      }

      @Override
      public UpdateSubscription setKey(java.lang.String key) {
        return (UpdateSubscription) super.setKey(key);
      }

      @Override
      public UpdateSubscription setOauthToken(java.lang.String oauthToken) {
        return (UpdateSubscription) super.setOauthToken(oauthToken);
      }

      @Override
      public UpdateSubscription setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (UpdateSubscription) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public UpdateSubscription setQuotaUser(java.lang.String quotaUser) {
        return (UpdateSubscription) super.setQuotaUser(quotaUser);
      }

      @Override
      public UpdateSubscription setUserIp(java.lang.String userIp) {
        return (UpdateSubscription) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.Long id;

      /**

       */
      public java.lang.Long getId() {
        return id;
      }

      public UpdateSubscription setId(java.lang.Long id) {
        this.id = id;
        return this;
      }

      @com.google.api.client.util.Key
      private java.lang.String gcm;

      /**

       */
      public java.lang.String getGcm() {
        return gcm;
      }

      public UpdateSubscription setGcm(java.lang.String gcm) {
        this.gcm = gcm;
        return this;
      }

      @Override
      public UpdateSubscription set(String parameterName, Object value) {
        return (UpdateSubscription) super.set(parameterName, value);
      }
    }

  }

  /**
   * An accessor for creating requests from the Sender collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Alertendpoint alertendpoint = new Alertendpoint(...);}
   *   {@code Alertendpoint.Sender.List request = alertendpoint.sender().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public Sender sender() {
    return new Sender();
  }

  /**
   * The "sender" collection of methods.
   */
  public class Sender {

    /**
     * Create a request for the method "sender.list".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link List#execute()} method to invoke the remote operation.
     *
     * @return the request
     */
    public List list() throws java.io.IOException {
      List result = new List();
      initialize(result);
      return result;
    }

    public class List extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.SenderCollection> {

      private static final String REST_PATH = "senderList";

      /**
       * Create a request for the method "sender.list".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link List#execute()} method to invoke the remote operation. <p>
       * {@link List#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
       * must be called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @since 1.13
       */
      protected List() {
        super(Alertendpoint.this, "POST", REST_PATH, null, com.appspot.hazard_alert.alertendpoint.model.SenderCollection.class);
      }

      @Override
      public List setAlt(java.lang.String alt) {
        return (List) super.setAlt(alt);
      }

      @Override
      public List setFields(java.lang.String fields) {
        return (List) super.setFields(fields);
      }

      @Override
      public List setKey(java.lang.String key) {
        return (List) super.setKey(key);
      }

      @Override
      public List setOauthToken(java.lang.String oauthToken) {
        return (List) super.setOauthToken(oauthToken);
      }

      @Override
      public List setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (List) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public List setQuotaUser(java.lang.String quotaUser) {
        return (List) super.setQuotaUser(quotaUser);
      }

      @Override
      public List setUserIp(java.lang.String userIp) {
        return (List) super.setUserIp(userIp);
      }

      @Override
      public List set(String parameterName, Object value) {
        return (List) super.set(parameterName, value);
      }
    }

  }

  /**
   * An accessor for creating requests from the Subscription collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Alertendpoint alertendpoint = new Alertendpoint(...);}
   *   {@code Alertendpoint.Subscription.List request = alertendpoint.subscription().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public Subscription subscription() {
    return new Subscription();
  }

  /**
   * The "subscription" collection of methods.
   */
  public class Subscription {

    /**
     * Create a request for the method "subscription.create".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link Create#execute()} method to invoke the remote operation.
     *
     * @param gcm
     * @param expires
     * @param content the {@link com.appspot.hazard_alert.alertendpoint.model.Bounds}
     * @return the request
     */
    public Create create(java.lang.String gcm, java.lang.Long expires, com.appspot.hazard_alert.alertendpoint.model.Bounds content) throws java.io.IOException {
      Create result = new Create(gcm, expires, content);
      initialize(result);
      return result;
    }

    public class Create extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.Subscription> {

      private static final String REST_PATH = "createSubscription/{gcm}/{expires}";

      /**
       * Create a request for the method "subscription.create".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link Create#execute()} method to invoke the remote operation.
       * <p> {@link
       * Create#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
       * be called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @param gcm
       * @param expires
       * @param content the {@link com.appspot.hazard_alert.alertendpoint.model.Bounds}
       * @since 1.13
       */
      protected Create(java.lang.String gcm, java.lang.Long expires, com.appspot.hazard_alert.alertendpoint.model.Bounds content) {
        super(Alertendpoint.this, "POST", REST_PATH, content, com.appspot.hazard_alert.alertendpoint.model.Subscription.class);
        this.gcm = com.google.api.client.util.Preconditions.checkNotNull(gcm, "Required parameter gcm must be specified.");
        this.expires = com.google.api.client.util.Preconditions.checkNotNull(expires, "Required parameter expires must be specified.");
      }

      @Override
      public Create setAlt(java.lang.String alt) {
        return (Create) super.setAlt(alt);
      }

      @Override
      public Create setFields(java.lang.String fields) {
        return (Create) super.setFields(fields);
      }

      @Override
      public Create setKey(java.lang.String key) {
        return (Create) super.setKey(key);
      }

      @Override
      public Create setOauthToken(java.lang.String oauthToken) {
        return (Create) super.setOauthToken(oauthToken);
      }

      @Override
      public Create setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (Create) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public Create setQuotaUser(java.lang.String quotaUser) {
        return (Create) super.setQuotaUser(quotaUser);
      }

      @Override
      public Create setUserIp(java.lang.String userIp) {
        return (Create) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.String gcm;

      /**

       */
      public java.lang.String getGcm() {
        return gcm;
      }

      public Create setGcm(java.lang.String gcm) {
        this.gcm = gcm;
        return this;
      }

      @com.google.api.client.util.Key
      private java.lang.Long expires;

      /**

       */
      public java.lang.Long getExpires() {
        return expires;
      }

      public Create setExpires(java.lang.Long expires) {
        this.expires = expires;
        return this;
      }

      @Override
      public Create set(String parameterName, Object value) {
        return (Create) super.set(parameterName, value);
      }
    }
    /**
     * Create a request for the method "subscription.get".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link Get#execute()} method to invoke the remote operation.
     *
     * @param id
     * @return the request
     */
    public Get get(java.lang.Long id) throws java.io.IOException {
      Get result = new Get(id);
      initialize(result);
      return result;
    }

    public class Get extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.Subscription> {

      private static final String REST_PATH = "subscriptionGet/{id}";

      /**
       * Create a request for the method "subscription.get".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link Get#execute()} method to invoke the remote operation. <p>
       * {@link Get#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
       * must be called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @param id
       * @since 1.13
       */
      protected Get(java.lang.Long id) {
        super(Alertendpoint.this, "POST", REST_PATH, null, com.appspot.hazard_alert.alertendpoint.model.Subscription.class);
        this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
      }

      @Override
      public Get setAlt(java.lang.String alt) {
        return (Get) super.setAlt(alt);
      }

      @Override
      public Get setFields(java.lang.String fields) {
        return (Get) super.setFields(fields);
      }

      @Override
      public Get setKey(java.lang.String key) {
        return (Get) super.setKey(key);
      }

      @Override
      public Get setOauthToken(java.lang.String oauthToken) {
        return (Get) super.setOauthToken(oauthToken);
      }

      @Override
      public Get setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (Get) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public Get setQuotaUser(java.lang.String quotaUser) {
        return (Get) super.setQuotaUser(quotaUser);
      }

      @Override
      public Get setUserIp(java.lang.String userIp) {
        return (Get) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.Long id;

      /**

       */
      public java.lang.Long getId() {
        return id;
      }

      public Get setId(java.lang.Long id) {
        this.id = id;
        return this;
      }

      @Override
      public Get set(String parameterName, Object value) {
        return (Get) super.set(parameterName, value);
      }
    }
    /**
     * Create a request for the method "subscription.updateExpires".
     *
     * This request holds the parameters needed by the the alertendpoint server.  After setting any
     * optional parameters, call the {@link UpdateExpires#execute()} method to invoke the remote
     * operation.
     *
     * @param id
     * @param expires
     * @return the request
     */
    public UpdateExpires updateExpires(java.lang.Long id, java.lang.Long expires) throws java.io.IOException {
      UpdateExpires result = new UpdateExpires(id, expires);
      initialize(result);
      return result;
    }

    public class UpdateExpires extends AlertendpointRequest<com.appspot.hazard_alert.alertendpoint.model.Subscription> {

      private static final String REST_PATH = "subscriptionUpdateExpires/{id}/{expires}";

      /**
       * Create a request for the method "subscription.updateExpires".
       *
       * This request holds the parameters needed by the the alertendpoint server.  After setting any
       * optional parameters, call the {@link UpdateExpires#execute()} method to invoke the remote
       * operation. <p> {@link UpdateExpires#initialize(com.google.api.client.googleapis.services.Abstra
       * ctGoogleClientRequest)} must be called to initialize this instance immediately after invoking
       * the constructor. </p>
       *
       * @param id
       * @param expires
       * @since 1.13
       */
      protected UpdateExpires(java.lang.Long id, java.lang.Long expires) {
        super(Alertendpoint.this, "POST", REST_PATH, null, com.appspot.hazard_alert.alertendpoint.model.Subscription.class);
        this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
        this.expires = com.google.api.client.util.Preconditions.checkNotNull(expires, "Required parameter expires must be specified.");
      }

      @Override
      public UpdateExpires setAlt(java.lang.String alt) {
        return (UpdateExpires) super.setAlt(alt);
      }

      @Override
      public UpdateExpires setFields(java.lang.String fields) {
        return (UpdateExpires) super.setFields(fields);
      }

      @Override
      public UpdateExpires setKey(java.lang.String key) {
        return (UpdateExpires) super.setKey(key);
      }

      @Override
      public UpdateExpires setOauthToken(java.lang.String oauthToken) {
        return (UpdateExpires) super.setOauthToken(oauthToken);
      }

      @Override
      public UpdateExpires setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (UpdateExpires) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public UpdateExpires setQuotaUser(java.lang.String quotaUser) {
        return (UpdateExpires) super.setQuotaUser(quotaUser);
      }

      @Override
      public UpdateExpires setUserIp(java.lang.String userIp) {
        return (UpdateExpires) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.Long id;

      /**

       */
      public java.lang.Long getId() {
        return id;
      }

      public UpdateExpires setId(java.lang.Long id) {
        this.id = id;
        return this;
      }

      @com.google.api.client.util.Key
      private java.lang.Long expires;

      /**

       */
      public java.lang.Long getExpires() {
        return expires;
      }

      public UpdateExpires setExpires(java.lang.Long expires) {
        this.expires = expires;
        return this;
      }

      @Override
      public UpdateExpires set(String parameterName, Object value) {
        return (UpdateExpires) super.set(parameterName, value);
      }
    }

  }

  /**
   * Builder for {@link Alertendpoint}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link Alertendpoint}. */
    @Override
    public Alertendpoint build() {
      return new Alertendpoint(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link AlertendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setAlertendpointRequestInitializer(
        AlertendpointRequestInitializer alertendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(alertendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
