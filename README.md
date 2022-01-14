# azure-sample-java

Azure sample code in java, for example how to get a SAS token by using the user delegated key.

## User Delegation SAS
SAS (Shared Access Tokens) allows accessing the storage account resources for various operations, although Azure AD based authorization is much more secure some of the client application would still want to use a SAS token for the storage operations.

For example, there is a web app which generates the download URL for some resources for a user. Now imaging that you only want to create downloadable URL which could be valid for certain time (a few hour or a couple of days?). SAS tokens become very handy for that.

You can create a SAS token for particular scope and append in the storage resource's URL. For example https://storageaccountname.blob.core.windows.net/resourcecontainer/image.jpg is the resource, you can further add SAS as querystring in the URL and it will give user ability to download the file.

Another scenario could be where a legacy application can only use https endpoint to download or upload the file without anything else. 

This example code could be extended for any further use. It is just to demonstrate that using azure sdk is as simple and easy as using one of your own java class. For further details and information please refer javadoc for the azure sdk (Azure identity and azure storage library particularly).

## Uses 

There is a build task configured for the VS code, you can compile and create a jar file. Once azure-sample-java.jar file has been created, navigate the the folder and run the jar as following:

```
java -jar .\azure-sample-java.jar -a STORAGEACCOUNTNAME -b BLOBCONTAINER_NAME -c CLIENT_ID_OF_YOUR_SPN  -s SECRET_OF_SPN -t YOURTENANT.onmicrosoft.com

```

**Note**: please replace values as per your environment. 
