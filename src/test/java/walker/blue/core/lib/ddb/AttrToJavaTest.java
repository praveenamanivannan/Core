package walker.blue.core.lib.ddb;

import com.amazonaws.services.dynamodbv2.model.GetItemResult;

import junit.framework.Assert;

import org.junit.Test;

import java.io.File;

import walker.blue.core.lib.types.Building;

/**
 * Unit test for AttrToJava class
 */
public class AttrToJavaTest {

    private static final String CREDENTIALS_PATH = "../../../awscredentials";
    private static final String NOT_RUN = "Test was not run since the credentials file was not found";
    private static final String BUILDING_ID = "TEST_NEW_MAP";

    @Test
    public void testAttrToBuilding() {
        File credFile = new File(CREDENTIALS_PATH);
        if(!credFile.exists() || credFile.isDirectory()) {
            System.out.println(NOT_RUN);
            return;
        }
        DynamoDBWrapper ddb = new DynamoDBWrapper(AWSCredentialsLoader.loadCredentials(CREDENTIALS_PATH));
        GetItemResult data = ddb.getBuildingData(BUILDING_ID);
        Building building = AttrToJava.getItemResultToBuilding(data);
        Assert.assertNotNull(building);
        System.out.println(building.toString());
    }
}