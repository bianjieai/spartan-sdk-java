package irita.sdk;

import com.google.protobuf.GeneratedMessageV3;
import irita.sdk.client.BaseClient;
import irita.sdk.client.IritaClient;
import irita.sdk.constant.enums.BroadcastMode;
import irita.sdk.crypto.eth.LegacyTransaction;
import irita.sdk.model.*;
import irita.sdk.model.block.BlockDetail;
import irita.sdk.model.tx.Condition;
import irita.sdk.model.tx.EventQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import proto.nft.Tx;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ClientTest extends ConfigTest {
    private IritaClient client;

    @BeforeEach
    public void init() {
        client = getTestClient();
    }

    @Test
    @Disabled
    public void queryAccount() {
        String addr = "iaa1ytemz2xqq2s73ut3ys8mcd6zca2564a5lfhtm3";
        Account account = client.getBaseClient().queryAccount(addr);
        assertEquals(addr, account.getAddress());
    }

    @Test
    @Disabled
    public void simulateTx() throws IOException {
        BaseClient baseClient = client.getBaseClient();
        BaseTx baseTx = new BaseTx(10000, new Fee("10000", "ugas"), BroadcastMode.Commit);
        Account account = baseClient.queryAccount(baseTx);
        Tx.MsgIssueDenom msg = Tx.MsgIssueDenom
                .newBuilder()
                .setId("testfjdsklf21A3")
                .setName("testfjdsklf213")
                .setSchema("nullschema")
                .setSender(account.getAddress())
                .build();

        List<GeneratedMessageV3> msgs = Collections.singletonList(msg);
        GasInfo gasInfo = baseClient.simulateTx(msgs, baseTx, null);
        System.out.println(gasInfo);
    }

    @Test
    @Disabled
    public void queryTx() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String hash = "EC3C341C319EC4A5AAAA77732B3016CACFC7FD68A19656FB70DFFF85AFC4C4E1";//tibc
        ResultQueryTx resultQueryTx = client.getBaseClient().queryTx(hash);
        assertNotNull(resultQueryTx);
    }

    @Test
    @Disabled
    public void queryTxEvmGetSender() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ResultQueryTx result = client.getBaseClient().queryTx("4B6E1F2BA9B93CF51D354087348527FC21857AB190EBC6A5A20FCEF0D9F49687");
        List<GeneratedMessageV3> messageList = result.getTx().getBody().getMsgs();
        for (GeneratedMessageV3 generatedMessageV3 : messageList){
            proto.ethermint.evm.v1.Tx.MsgEthereumTx msgEthereumTx =proto.ethermint.evm.v1.Tx.MsgEthereumTx.parseFrom (generatedMessageV3.toByteString());
            proto.ethermint.evm.v1.Tx.LegacyTx legacyTx = proto.ethermint.evm.v1.Tx.LegacyTx.parseFrom(msgEthereumTx.getData().getValue());
            LegacyTransaction legacyTransaction = new LegacyTransaction(legacyTx);
            String addr = legacyTransaction.getSender();
            System.out.println(addr);
            assertEquals("iaa1qjcpag8wuw0pzm9qycn3athppp4k24ntgwmtl5",addr);
        }
    }

    @Test
    @Disabled
    public void queryTxs() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        EventQueryBuilder builder = EventQueryBuilder.newEventQueryBuilder()
                .AddCondition(Condition.newCond("tibc_nft_transfer", "sender").eq("iaa18e23vvukxgatgzm4fgqkdggxecurkf39ytw7ue"));
//                .AddCondition(Condition.newCond("send_packet", "packet_sequence").eq(4));
        int page = 1;
        int size = 10;
        ResultSearchTxs resultSearchTxs = client.getBaseClient().queryTxs(builder, page, size);
        assertNotNull(resultSearchTxs);
    }

    @Test
    @Disabled
    public void queryBlock() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        BlockDetail blockDetail = client.getBaseClient().queryBlock("33105");
        assertNotNull(blockDetail);
    }
}
