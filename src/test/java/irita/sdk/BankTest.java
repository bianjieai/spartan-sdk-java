package irita.sdk;

import irita.sdk.client.IritaClient;
import irita.sdk.constant.enums.BroadcastMode;
import irita.sdk.exception.IritaSDKException;
import irita.sdk.key.KeyManager;
import irita.sdk.key.KeyManagerFactory;
import irita.sdk.model.*;
import irita.sdk.module.bank.BankClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BankTest extends ConfigTest {
	private BankClient bankClient;
	private final BaseTx baseTx = new BaseTx(200000, new Fee("300000", "ugas"), BroadcastMode.Commit);

	@BeforeEach
	public void init() {
		IritaClient client = getTestClient();
		bankClient = client.getBankClient();
	}

	@Test
	@Disabled
	public void send() throws Exception {
		/*KeyManager termKm = KeyManagerFactory.createDefault();
		termKm.add();
		String addr = termKm.getCurrentKeyInfo().getAddress();*/
		String addr ="iaa1et97w6anxx8x7xq0vts8nl7a2e667qj4804r5f";

		ResultTx resultTx = bankClient.send("4000000000", "upoint", addr, baseTx);
		assertNotNull(resultTx.getResult().getHash());
		BaseAccount account = bankClient.queryAccount(addr);
		List<Coin> coins = account.getCoins();
		Optional<Coin> iritaCoin = coins.stream().filter(x -> x.getDenom().equals("upoint")).findFirst();
		if (!iritaCoin.isPresent()) {
			throw new IritaSDKException("uirita not found from list");
		}

		assertEquals(iritaCoin.get().getAmount(), "10");

	}
}
