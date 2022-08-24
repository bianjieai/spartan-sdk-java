package irita.sdk;

import irita.sdk.client.BaseClient;
import irita.sdk.client.IritaClient;
import irita.sdk.config.ClientConfig;
import irita.sdk.config.OpbConfig;
import irita.sdk.constant.enums.BroadcastMode;
import irita.sdk.key.AlgoEnum;
import irita.sdk.key.KeyManager;
import irita.sdk.key.KeyManagerFactory;
import irita.sdk.model.BaseTx;
import irita.sdk.model.Fee;
import irita.sdk.model.ResultTx;
import irita.sdk.module.mt.MsgAddIssueMTRequest;
import irita.sdk.module.mt.MsgMintMTRequest;
import irita.sdk.module.mt.MtClient;
import irita.sdk.module.nft.NftClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import proto.cosmos.base.query.v1beta1.Pagination;
import proto.cosmos.tx.v1beta1.TxOuterClass;
import proto.mt.Mt;
import proto.mt.QueryOuterClass;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MtTest {

    private KeyManager km;
    private MtClient mtClient;
    private BaseClient baseClient;
    private final BaseTx baseTx = new BaseTx(400000, new Fee("400000", "ugas"), BroadcastMode.Commit);

    @BeforeEach
    public void init() {
        //更换为自己链上地址的助记词
        String mnemonic = "original resemble ritual rhythm loop climb toward junk call celery scale wire combine lamp matrix panther gallery waste garbage brisk long prosper empty actor";
        km = KeyManagerFactory.createKeyManger(AlgoEnum.ETH_SECP256K1);
        km.recover(mnemonic);

        //连接测试网（连接主网请参考README.md）
        String nodeUri = "http://127.0.0.1:26657";
        String grpcAddr = "127.0.0.1:9090";
        String chainId = "spartan";
        ClientConfig clientConfig = new ClientConfig(nodeUri, grpcAddr, chainId);
        //测试网为null，主网请参考README.md
        OpbConfig opbConfig = null;

        IritaClient client = new IritaClient(clientConfig, opbConfig, km);
        mtClient = client.getMtClient();
        baseClient = client.getBaseClient();
        //判断由助记词恢复的是否为预期的链上地址
        assertEquals("iaa1et97w6anxx8x7xq0vts8nl7a2e667qj4804r5f", km.getCurrentKeyInfo().getAddress());
    }

    @Test
    @Disabled
    public void testIssueDenom() throws IOException {
        String name = "test_issus_denom_3";
        String data = "issue_denom_data_3";
        ResultTx resultTx = mtClient.issueDenom(name, data.getBytes(), baseTx);
        System.out.println(resultTx.getResult().getHash());
        System.out.println(resultTx.getResult().getDeliver_tx().getLog());
    }

    /**
     * [{"events":[{"type":"issue_denom","attributes":[{"key":"denom_id","value":"a70782a65ed100a761a933e0a01ec73922825be27f8f03076a2ed7b1db0bcad7"},{"key":"denom_name","value":"test_issus_denom_1"},{"key":"owner","value":"iaa1w4yynxjyqyjkqmap4ug99l40zzhexykxmqf0mt"}]},{"type":"message","attributes":[{"key":"action","value":"/irismod.mt.MsgIssueDenom"},{"key":"module","value":"mt"},{"key":"sender","value":"iaa1w4yynxjyqyjkqmap4ug99l40zzhexykxmqf0mt"}]}]}]
     */

    @Test
    @Disabled
    public void testTransferDenom() throws IOException {
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        String recipient = "iaa1et97w6anxx8x7xq0vts8nl7a2e667qj4804r5f"; //account1
        ResultTx resultTx = mtClient.transferDenom(denomId, recipient, baseTx);
        System.out.println(resultTx.getResult().getHash());
        System.out.println(resultTx.getResult().getDeliver_tx().getLog());
    }

    @Test
    @Disabled
    public void testQueryDenoms() {
        Pagination.PageRequest pageRequest = Pagination.PageRequest.newBuilder()
                .setLimit(100)
                .setOffset(0)
                .build();
        QueryOuterClass.QueryDenomsResponse queryDenomsResponse = mtClient.queryDenoms(pageRequest);
        for (Mt.Denom denom : queryDenomsResponse.getDenomsList()) {
            System.out.println("denomId=" + denom.getId() + ",name=" + denom.getName() + ",owner=" + denom.getOwner());
        }
        System.out.println("total size = " + queryDenomsResponse.getDenomsCount());
    }

    @Test
    @Disabled
    public void testQueryDenom() {
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        QueryOuterClass.QueryDenomResponse queryDenomResponse = mtClient.queryDenom(denomId);
        System.out.println("denomId = " + queryDenomResponse.getDenom().getId() + ",name = " + queryDenomResponse.getDenom().getName() + ",owner = " + queryDenomResponse.getDenom().getOwner());
    }

    @Test
    @Disabled
    public void testMintMT() throws IOException {
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        String data = "mint_mt_data_" + new Random().nextInt(1000);
        MsgMintMTRequest msgMintMTRequest = new MsgMintMTRequest();
        msgMintMTRequest.setAmount(20);
        msgMintMTRequest.setDenomId(denomId);
        msgMintMTRequest.setData(data.getBytes());
        msgMintMTRequest.setRecipient("iaa1w4yynxjyqyjkqmap4ug99l40zzhexykxmqf0mt");
        ResultTx resultTx = mtClient.mintMT(msgMintMTRequest, baseTx);
        System.out.println(resultTx.getResult().getHash());
        System.out.println(resultTx.getResult().getDeliver_tx().getLog());
    }

    @Test
    @Disabled
    public void testAddIssueMt()throws IOException{
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        MsgAddIssueMTRequest msgMintMTRequest = new MsgAddIssueMTRequest();
        msgMintMTRequest.setId("ff6e57b41cb52ae7d58d854b2123da2c5657fd15d525821a13fe7da1b9cebd80");
        msgMintMTRequest.setAmount(20);
        msgMintMTRequest.setDenomId(denomId);
        msgMintMTRequest.setRecipient("iaa1et97w6anxx8x7xq0vts8nl7a2e667qj4804r5f");
        ResultTx resultTx = mtClient.additionalIssueMt(msgMintMTRequest, baseTx);
        System.out.println(resultTx.getResult().getHash());
        System.out.println(resultTx.getResult().getDeliver_tx().getLog());
    }

    @Test
    @Disabled
    public void testQueryMTSupply() {
        String mtID = "ff6e57b41cb52ae7d58d854b2123da2c5657fd15d525821a13fe7da1b9cebd80";
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        long amount = mtClient.queryMTSupply(denomId, mtID);
        System.out.println(amount);
    }

    @Test
    @Disabled
    public void testQueryMTS() {
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        Pagination.PageRequest pageRequest = Pagination.PageRequest.newBuilder()
                .setLimit(100)
                .setOffset(0)
                .build();
        QueryOuterClass.QueryMTsResponse queryMTsResponse = mtClient.queryMTS(denomId, pageRequest);
        for (Mt.MT mt : queryMTsResponse.getMtsList()) {
            System.out.println("mt_id = " + mt.getId() + ", mt_data = " + mt.getData().toStringUtf8() + ", mt_amount = " + mt.getSupply());
        }
        System.out.println("total_mt_count = " + queryMTsResponse.getMtsCount());
    }

    //todo need run test
    @Test
    @Disabled
    public void testQuerryMT() {
        String mtID = "ff6e57b41cb52ae7d58d854b2123da2c5657fd15d525821a13fe7da1b9cebd80";
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        QueryOuterClass.QueryMTResponse queryMTResponse = mtClient.querryMT(denomId, mtID);
        System.out.println("mt_id = " + queryMTResponse.getMt().getId() + ", mt_data = " + queryMTResponse.getMt().getData().toStringUtf8() + ", mt_amount = " + queryMTResponse.getMt().getSupply());
    }

    @Test
    @Disabled
    public void testQueryBalances() {
        String denomID = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        String owner = "iaa1et97w6anxx8x7xq0vts8nl7a2e667qj4804r5f";
        Pagination.PageRequest pageRequest = Pagination.PageRequest.newBuilder()
                .setLimit(100)
                .setOffset(0)
                .build();
        QueryOuterClass.QueryBalancesResponse queryBalancesResponse = mtClient.queryBalances(owner, denomID, pageRequest);
        for (Mt.Balance balance : queryBalancesResponse.getBalanceList()) {
            System.out.println("mt_id = " + balance.getMtId() + ", amount = " + balance.getAmount());
        }
    }

    @Test
    @Disabled
    public void testEditMT() throws IOException {
        String mtID = "ff6e57b41cb52ae7d58d854b2123da2c5657fd15d525821a13fe7da1b9cebd80";
        String denomId = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        String dataStr = "mt_edit_data_01";
        ResultTx resultTx = mtClient.editMT(mtID, denomId, dataStr.getBytes(), baseTx);
        System.out.println(resultTx.getResult().getHash());
        System.out.println(resultTx.getResult().getDeliver_tx().getLog());
    }

    @Test
    @Disabled
    public void testTransferMT() throws IOException {
        String mtId = "ff6e57b41cb52ae7d58d854b2123da2c5657fd15d525821a13fe7da1b9cebd80";
        String denomID = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        String recipient = "iaa1et97w6anxx8x7xq0vts8nl7a2e667qj4804r5f";
        long amount = 10;
        ResultTx resultTx = mtClient.transferMT(mtId, denomID, amount, recipient, baseTx);
        System.out.println(resultTx.getResult().getHash());
        System.out.println(resultTx.getResult().getDeliver_tx().getLog());
    }

    @Test
    @Disabled
    public void testBurnMT() throws IOException {
        String mtId = "ff6e57b41cb52ae7d58d854b2123da2c5657fd15d525821a13fe7da1b9cebd80";
        String denomID = "c02a799c8fee067a7f9b944554d8431ee539847234441833e45a3a2d3123fd99";
        long burnAmount = 5;
        ResultTx resultTx = mtClient.burnMT(mtId,denomID,burnAmount,baseTx);
        System.out.println(resultTx.getResult().getHash());
        System.out.println(resultTx.getResult().getDeliver_tx().getLog());
    }

    /*
    querySupply() Temporarily unavailable
     */
    /*@Test
    @Disabled
    public void testQuerySupply(){
        String denomID = "259edd57e552854d42bc4a0d98dc7a48fddeae343ad428c9df1d4b09e0ab525a";
        String owmer = "iaa1w4yynxjyqyjkqmap4ug99l40zzhexykxmqf0mt";
        long amount = mtClient.querySupply(denomID,owmer);
        System.out.println(amount);
    }*/

}
