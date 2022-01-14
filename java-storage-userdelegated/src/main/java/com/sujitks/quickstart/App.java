package com.sujitks.quickstart;

import java.time.OffsetDateTime;
import java.util.Locale;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;




/**
 
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Options options = new Options();

        Option tenant = new Option("t", "tenant", true, "Azure Tenant");
        tenant .setRequired(true);
        options.addOption(tenant);

        Option clientId = new Option("c", "clientid", true, "SPN Client ID");
        clientId.setRequired(true);
        options.addOption(clientId);

        Option secret = new Option("s", "secret", true, "SPN's secret");
        secret.setRequired(true);
        options.addOption(secret);

        Option storageaccount = new Option("a", "account", true, "storage accoun");
        storageaccount.setRequired(true);
        options.addOption(storageaccount);

        Option container = new Option("b", "blobcontainer", true, "blob container");
        container.setRequired(true);
        options.addOption(container);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java-storage-userdelegated", options);

            System.exit(1);
            return;
        }
        String strClientId = cmd.getOptionValue("clientid");
        String strTenant = cmd.getOptionValue("tenant");
        String strSecret = cmd.getOptionValue("secret");
        String strStorageAccount = cmd.getOptionValue("account");
        String strContainerName = cmd.getOptionValue("blobcontainer");

        System.out.println( getSasToken(strClientId, strTenant, strSecret, strStorageAccount, strContainerName) );
    }

    private static  String getSasToken(String strClientId, String  strTenant,String  strSecret,String  strStorageAccount,String  strContainerName)
    {
        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", strStorageAccount);
        ClientSecretCredential  creds = new ClientSecretCredentialBuilder().clientId(strClientId).tenantId(strTenant).clientSecret(strSecret).build();
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().endpoint(endpoint)
                   .credential(creds).buildClient();
        // Get a user delegation key for the Blob service that's valid for seven days.
        OffsetDateTime keyStart = OffsetDateTime.now();
        OffsetDateTime keyExpiry = OffsetDateTime.now().plusDays(7);
        UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(keyStart, keyExpiry);

        BlobContainerSasPermission blobContainerSas = new BlobContainerSasPermission();
        blobContainerSas.setReadPermission(true);
        BlobServiceSasSignatureValues blobServiceSasSignatureValues = new BlobServiceSasSignatureValues(keyExpiry,
                blobContainerSas);
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(strContainerName);
        if (!blobContainerClient.exists())
            blobContainerClient.create();

        String sas = blobContainerClient
                .generateUserDelegationSas(blobServiceSasSignatureValues, userDelegationKey);
                return sas;
    }
}
