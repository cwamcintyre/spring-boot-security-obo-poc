# Azure AD OBO Flow for Web Apps

## Setup

1. **Expose a Custom Scope in Your Web App Registration**
   - In Azure Portal, go to your web app registration → Expose an API.
   - Set the Application ID URI (e.g., `api://<web-app-client-id>`).
   - Add a custom scope (e.g., `obo_grant`).
   - Set `"accessTokenAcceptedVersion": 2` in the manifest for both the web app and API app registrations.

2. **Login Flow**
   - Ensure that the scopes requested during login INCLUDE the custom scope you created (e.g., `api://<web-app-client-id>/obo_grant`).
   - This will ensure that the audience of the token is for your app.

3. **OBO Flow**
   - Use the user access token (with your app as the audience) as the assertion in the OBO flow to get a token for your API.
   - The OBO token will be v2 if the API app registration manifest is set correctly.

## Key Points

- You cannot get a user access token for your app directly from the login flow if you only request OIDC scopes (openid, profile, email).
- You must use a custom scope to get a user access token for your app.
- Both the web app and API app registrations must have `"accessTokenAcceptedVersion": 2` in their manifests to get v2 tokens.
- The OBO flow will only work if the assertion token is for your app and is v2.

## Microsoft Documentation

- [Expose an API in Azure AD](https://learn.microsoft.com/en-us/azure/active-directory/develop/quickstart-configure-app-expose-web-apis)
- [On-Behalf-Of (OBO) flow](https://learn.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow)
- [accessTokenAcceptedVersion property](https://learn.microsoft.com/en-us/azure/active-directory/develop/reference-app-manifest#accessTokenAcceptedVersion-attribute)
