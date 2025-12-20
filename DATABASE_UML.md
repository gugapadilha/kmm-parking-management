# Diagrama UML do Banco de Dados

## Modelo de Dados - Gestão de Estacionamento

```
┌─────────────────────────────────────────────────────────────┐
│                    vehicles                                 │
├─────────────────────────────────────────────────────────────┤
│ + id: Long (PK, AutoIncrement)                              │
│ + plate: String                                             │
│ + model: String                                             │
│ + color: String                                             │
│ + priceTableId: Long (FK -> price_tables.id)               │
│ + entryDateTime: Long (Timestamp)                           │
│ + exitDateTime: Long? (Timestamp, nullable)                │
│ + totalAmount: Double? (nullable)                           │
│ + paymentMethodId: Long? (FK -> payment_methods.id)        │
│ + isInParkingLot: Boolean                                   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                 price_tables                                │
├─────────────────────────────────────────────────────────────┤
│ + id: Long (PK)                                             │
│ + name: String                                              │
│ + initialTolerance: String (HH:mm)                          │
│ + untilTime: String? (HH:mm, nullable)                      │
│ + untilValue: Double? (nullable)                            │
│ + fromTime: String? (HH:mm, nullable)                      │
│ + everyInterval: String? (HH:mm, nullable)                 │
│ + addValue: Double? (nullable)                              │
│ + maxChargePeriod: String? (HH:mm, nullable)               │
│ + maxChargeValue: Double? (nullable)                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                payment_methods                              │
├─────────────────────────────────────────────────────────────┤
│ + id: Long (PK)                                             │
│ + name: String                                              │
│ + description: String? (nullable)                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    payments                                 │
├─────────────────────────────────────────────────────────────┤
│ + id: Long (PK, AutoIncrement)                             │
│ + vehicleId: Long (FK -> vehicles.id)                      │
│ + paymentMethodId: Long (FK -> payment_methods.id)         │
│ + amount: Double                                            │
│ + dateTime: Long (Timestamp)                                │
└─────────────────────────────────────────────────────────────┘
```

## Relacionamentos

1. **vehicles** → **price_tables** (Many-to-One)
   - Um veículo possui uma tabela de preços
   - Uma tabela de preços pode ser usada por vários veículos

2. **vehicles** → **payment_methods** (Many-to-One, opcional)
   - Um veículo pode ter uma forma de pagamento (quando sai do pátio)
   - Uma forma de pagamento pode ser usada por vários veículos

3. **payments** → **vehicles** (Many-to-One)
   - Um pagamento pertence a um veículo
   - Um veículo pode ter vários pagamentos (teoricamente, mas no contexto atual, um por saída)

4. **payments** → **payment_methods** (Many-to-One)
   - Um pagamento possui uma forma de pagamento
   - Uma forma de pagamento pode ser usada em vários pagamentos

## Descrição das Tabelas

### vehicles
Armazena todas as informações dos veículos que entraram no estacionamento. O campo `isInParkingLot` indica se o veículo ainda está no pátio (true) ou já saiu (false).

### price_tables
Armazena as tabelas de preços sincronizadas da API. Contém todas as regras de cobrança:
- `initialTolerance`: Tolerância inicial antes de começar a cobrar
- `untilTime` / `untilValue`: Valor fixo até determinado tempo
- `fromTime` / `everyInterval` / `addValue`: Valor adicional a cada intervalo após determinado tempo
- `maxChargePeriod` / `maxChargeValue`: Valor máximo a ser cobrado em determinado período

### payment_methods
Armazena as formas de pagamento disponíveis sincronizadas da API (ex: Dinheiro, Crédito, Débito, etc.).

### payments
Registra todos os pagamentos realizados quando veículos saem do pátio. Mantém histórico de todas as transações.

## Índices Sugeridos

- `vehicles.priceTableId` (FK)
- `vehicles.paymentMethodId` (FK)
- `vehicles.isInParkingLot` (para consultas frequentes)
- `payments.vehicleId` (FK)
- `payments.paymentMethodId` (FK)

## Observações

- Todos os timestamps são armazenados como Long (milliseconds desde epoch)
- Os valores monetários são armazenados como Double
- As tabelas `price_tables` e `payment_methods` são populadas via sincronização com a API
- As tabelas `vehicles` e `payments` são gerenciadas localmente pelo aplicativo

