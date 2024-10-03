package org.example.assetmanagement; 
import org.hyperledger.fabric.contract.annotation.Default; 
import org.hyperledger.fabric.contract.annotation.Property; 
import org.hyperledger.fabric.contract.annotation.Transaction; 
import org.hyperledger.fabric.contract.ContractInterface; 
import org.hyperledger.fabric.contract.Context; 
import java.util.HashMap; 
import java.util.Map; 
@Default 
public class AssetManagement implements ContractInterface { 
private final Map<String, Asset> assetList = new HashMap<>(); 
public static class Asset { 
public String dealerId; 
public String msisdn; 
public String mpin; 
public double balance; 
public String status; 
public double transAmount; 
public String transType; 
public String remarks; 
public Asset(String dealerId, String msisdn, String mpin, double balance, String 
status, double transAmount, String transType, String remarks) { 
this.dealerId = dealerId; 
this.msisdn = msisdn; 
this.mpin = mpin; 
this.balance = balance; 
this.status = status; 
this.transAmount = transAmount; 
this.transType = transType; 
this.remarks = remarks; 
} 
} 
@Transaction 
public void createAsset(Context ctx, String dealerId, String msisdn, String mpin, 
double balance, String status) { 
Asset asset = new Asset(dealerId, msisdn, mpin, balance, status, 0, "", ""); 
assetList.put(dealerId, asset); 
} 
@Transaction 
public Asset getAsset(Context ctx, String dealerId) { 
return assetList.get(dealerId); 
} 
@Transaction 
public void updateAsset(Context ctx, String dealerId, double transAmount, String 
transType, String remarks) { 
Asset asset = assetList.get(dealerId); 
if (asset != null) { 
asset.transAmount = transAmount; 
asset.transType = transType; 
asset.remarks = remarks; 
asset.balance += transAmount; // Update balance based on transaction type 
logic 
} 
} 
// You can implement more methods for querying, deleting, etc. 
} 
plugins { 
id 'java' 
id 'application' 
} 
repositories { 
mavenCentral(); 
} 
dependencies { 
implementation 'org.hyperledger.fabric:fabric-chaincode-java:2.4.0' 
} 
mainClassName = 'org.example.assetmanagement.AssetManagement'; 
public class AssetManagementApp { 
public static void main(String[] args) { 
// Initialize SDK and set up connection to the Fabric network 
// Call chaincode methods using the SDK to manage assets 
Asset s=new Asset(); 
S. Asset(String dealerId, String msisdn, String mpin, double balance, String 
status, double transAmount, String transType, String remarks); 
s. createAsset(Context ctx, String dealerId, String msisdn, String mpin, double 
balance, String status); 
s. updateAsset(Context ctx, String dealerId, double transAmount, String 
transType, String remarks); 
} 
} 
Level-2 Develop and test the smart contract for the above 
requirement 
package org.example.assetmanagement; 
import org.hyperledger.fabric.contract.annotation.Default; 
import org.hyperledger.fabric.contract.annotation.Transaction; 
import org.hyperledger.fabric.contract.Context; 
import org.hyperledger.fabric.contract.ContractInterface; 
import java.util.HashMap; 
import java.util.Map; 
@Default 
public class AssetManagement implements ContractInterface { 
private final Map<String, Asset> assetList = new HashMap<>(); 
public static class Asset { 
public String dealerId; 
public String msisdn; 
public String mpin; 
public double balance; 
public String status; 
public String transactionHistory; 
public Asset(String dealerId, String msisdn, String mpin, double balance, String 
status) { 
this.dealerId = dealerId; 
this.msisdn = msisdn; 
this.mpin = mpin; 
this.balance = balance; 
this.status = status; 
this.transactionHistory = ""; 
} 
} 
@Transaction 
public void createAsset(Context ctx, String dealerId, String msisdn, String mpin, 
double balance, String status) { 
Asset asset = new Asset(dealerId, msisdn, mpin, balance, status); 
assetList.put(dealerId, asset); 
} 
@Transaction 
public Asset getAsset(Context ctx, String dealerId) { 
return assetList.get(dealerId); 
} 
@Transaction 
public void updateAsset(Context ctx, String dealerId, double transAmount, String 
transType, String remarks) { 
Asset asset = assetList.get(dealerId); 
if (asset != null) { 
asset.balance += transAmount; // Update balance 
asset.transactionHistory += String.format("Type: %s, Amount: %.2f, Remarks: 
%s; ", transType, transAmount, remarks); 
} 
} 
@Transaction 
public String getTransactionHistory(Context ctx, String dealerId) { 
Asset asset = assetList.get(dealerId); 
return asset != null ? asset.transactionHistory : "No transactions found."; 
} 
} 
// 
Create a pom.xml file in your asset-management-chaincode directory to manage 
your dependencies: 
<project xmlns="http://maven.apache.org/POM/4.0.0" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
http://maven.apache.org/xsd/maven-4.0.0.xsd"> 
<modelVersion>4.0.0</modelVersion> 
<groupId>org.example</groupId> 
<artifactId>asset-management-chaincode</artifactId> 
<version>1.0.0</version> 
<packaging>jar</packaging> 
<dependencies> 
<dependency> 
<groupId>org.hyperledger.fabric</groupId> 
<artifactId>fabric-chaincode-java</artifactId> 
<version>2.4.0</version> 
</dependency> 
</dependencies> 
</project> 
// y
 ou can test your chaincode by creating a simple Java 
client. Create a new Java file 
import org.hyperledger.fabric.gateway.*; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
public class AssetClient { 
public static void main(String[] args) throws Exception { 
// Load a file system wallet and create a gateway to interact with the peer 
Path walletPath = Paths.get("wallet"); 
Path networkConfigPath = Paths.get("network-config.yaml"); 
Wallet wallet = Wallets.newFileSystemWallet(walletPath); 
Gateway.Builder builder = Gateway.createBuilder(); 
try (Gateway gateway = builder.identity(wallet, 
"user1").networkConfig(networkConfigPath).connect()) { 
Network network = gateway.getNetwork("mychannel"); 
Contract contract = network.getContract("assetmanagement"); 
// Create an asset 
contract.submitTransaction("createAsset", "dealer1", "1234567890", "1234", 
"1000", "ACTIVE"); 
// Get asset 
byte[] result = contract.evaluateTransaction("getAsset", "dealer1"); 
System.out.println("Asset: " + new String(result)); 
// Update asset 
contract.submitTransaction("updateAsset", "dealer1", 500, "DEPOSIT", "Added 
funds"); 
// Get transaction history 
String history = contract.evaluateTransaction("getTransactionHistory", 
"dealer1"); 
System.out.println("Transaction History: " + history); 
} 
} 
} 
//vel-3 Develop a rest api for invoking smart contract deployed into hyperledger 
//fabric test network and create a docker image for the rest api 
<dependencies> 
<!-- Other dependencies --> 
<dependency> 
<groupId>org.hyperledger.fabric</groupId> 
<artifactId>fabric-gateway-java</artifactId> 
<version>2.4.0</version> 
</dependency> 
<dependency> 
<groupId>org.springframework.boot</groupId> 
<artifactId>spring-boot-starter-web</artifactId> 
</dependency> 
</dependencies> 
// Create a new Java class in the src/main/java/com/example/demo directory (adjust 
the package as necessary): 
package com.example.demo; 
import org.hyperledger.fabric.gateway.*; 
import org.springframework.web.bind.annotation.*; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
import java.util.concurrent.ExecutionException; 
@RestController 
@RequestMapping("/api/assets") 
public class AssetController { 
private Gateway gateway; 
private Contract contract; 
public AssetController() throws Exception { 
Path walletPath = Paths.get("wallet"); 
Path networkConfigPath = Paths.get("network-config.yaml"); 
Wallet wallet = Wallets.newFileSystemWallet(walletPath); 
Gateway.Builder builder = Gateway.createBuilder(); 
this.gateway = builder.identity(wallet, 
"user1").networkConfig(networkConfigPath).connect(); 
Network network = gateway.getNetwork("mychannel"); 
this.contract = network.getContract("assetmanagement"); 
} 
@PostMapping("/create") 
public String createAsset(@RequestParam String dealerId, @RequestParam String 
msisdn, 
@RequestParam String mpin, @RequestParam double balance, 
@RequestParam String status) throws Exception { 
contract.submitTransaction("createAsset", dealerId, msisdn, mpin, 
String.valueOf(balance), status); 
return "Asset created successfully!"; 
} 
@GetMapping("/{dealerId}") 
public String getAsset(@PathVariable String dealerId) throws Exception { 
byte[] result = contract.evaluateTransaction("getAsset", dealerId); 
return new String(result); 
} 
@PostMapping("/update") 
public String updateAsset(@RequestParam String dealerId, @RequestParam double 
transAmount, 
@RequestParam String transType, @RequestParam String remarks) 
throws Exception { 
contract.submitTransaction("updateAsset", dealerId, 
String.valueOf(transAmount), transType, remarks); 
return "Asset updated successfully!"; 
} 
@GetMapping("/history/{dealerId}") 
public String getTransactionHistory(@PathVariable String dealerId) throws 
Exception { 
byte[] result = contract.evaluateTransaction("getTransactionHistory", dealerId); 
return new String(result); 
} 
} 
// Create an Asset: 
curl -X POST 
http://localhost:8080/api/assets/create?dealerId=dealer1&msisdn=1234567890&mpi
 n=1234&balance=1000&status=ACTIVE 
Get an Asset 
curl -X GET http://localhost:8080/api/assets/dealer1 
Update an Asset: 
curl -X POST 
http://localhost:8080/api/assets/update?dealerId=dealer1&transAmount=500&trans
 Type=DEPOSIT&remarks=Added funds 
Get Transaction History: 
curl -X GET http://localhost:8080/api/assets/history/dealer1