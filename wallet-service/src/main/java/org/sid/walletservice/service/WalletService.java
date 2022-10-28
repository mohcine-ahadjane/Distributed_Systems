package org.sid.walletservice.service;

import org.sid.walletservice.entities.Currency;
import org.sid.walletservice.entities.Wallet;
import org.sid.walletservice.repositories.CurrencyRepository;
import org.sid.walletservice.repositories.WalletRepository;
import org.sid.walletservice.repositories.WalletTransactionRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Transactional
public class WalletService {
    private CurrencyRepository currencyRepository;
    private WalletRepository walletRepository;
    private WalletTransactionRepository walletTransactionRepository;

    public WalletService(CurrencyRepository currencyRepository, WalletRepository walletRepository, WalletTransactionRepository walletTransactionRepository) {
        this.currencyRepository = currencyRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    public void loadData() throws IOException {
        URI uri = new ClassPathResource("currencies.data.csv").getURI();
        Path path = Paths.get(uri);
        List<String> lines = Files.readAllLines(path);
        for(int i= 1; i<lines.size(); i++){
            String[] line= lines.get(i).split(";");
            Currency currency=Currency.builder()
                    .code(line[0])
                    .name(line[1])
                    .salePrice(Double.parseDouble(line[2]))
                    .purchasePrice(Double.parseDouble(line[3]))
                    .build();
            currencyRepository.save(currency);
        }
        Stream.of("MAD", "EUR", "USD", "CAD").forEach(currencyCode->{
            Currency currency= currencyRepository.findById(currencyCode)
                    .orElseThrow(()-> new RuntimeException(String.format("Currency % not found", currencyCode)));
            Wallet wallet = new Wallet();
            wallet.setBalance(10000.0);
            wallet.setCurrency(currency);
            wallet.setCreatedAt(System.currentTimeMillis());
            wallet.setUserId(UUID.randomUUID().toString());
            walletRepository.save(wallet);

        });
    }

}
