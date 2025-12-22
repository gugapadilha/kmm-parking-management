# Guia de Migração para Kotlin Multiplatform (KMP)

Este documento explica como o projeto foi configurado para funcionar tanto no Android quanto na Web usando Kotlin Multiplatform.

## Estrutura de Diretórios

```
app/src/
├── commonMain/          # Código compartilhado entre todas as plataformas
│   └── kotlin/
│       └── com/example/gestodeestacionamento/
│           ├── platform/        # Abstrações expect/actual
│           ├── data/           # (será movido)
│           ├── domain/         # (será movido)
│           └── presentation/   # (será movido)
│
├── androidMain/        # Código específico do Android
│   └── kotlin/
│       └── com/example/gestodeestacionamento/
│           ├── platform/        # Implementações Android
│           └── MainActivity.kt  # (será movido)
│
└── jsMain/             # Código específico da Web
    └── kotlin/
        └── com/example/gestodeestacionamento/
            ├── platform/        # Implementações Web
            └── Main.kt          # Ponto de entrada Web
```

## Arquivos Expect/Actual Criados

### 1. PlatformStorage
- **commonMain**: Interface expect para armazenamento
- **androidMain**: Implementação com DataStore
- **jsMain**: Implementação com LocalStorage

### 2. DatabaseFactory
- **commonMain**: Interface expect para banco de dados
- **androidMain**: Implementação com Room
- **jsMain**: Implementação com LocalStorage (simplificada)

### 3. Navigation
- **commonMain**: Função expect `rememberNavController()`
- **androidMain**: Implementação Android
- **jsMain**: Implementação Web

### 4. ViewModel
- **commonMain**: Função expect `koinViewModel()`
- **androidMain**: Implementação Android
- **jsMain**: Implementação Web

## Próximos Passos para Completar a Migração

### 1. Mover Código para commonMain

Os seguintes arquivos devem ser movidos para `commonMain/kotlin/`:

- `data/remote/` - API Service e DTOs
- `data/mapper/` - Mappers
- `data/repository/` - Repositories (exceto AuthRepositoryImpl)
- `domain/` - Models, Repositories interfaces, Use Cases
- `presentation/screen/` - Telas Compose
- `presentation/viewmodel/` - ViewModels
- `presentation/navigation/` - NavGraph
- `ui/theme/` - Tema

### 2. Mover Código para androidMain

- `MainActivity.kt`
- `data/local/` - Room Database, DAOs, Entities
- `data/repository/AuthRepositoryImpl.kt` (usa Context Android)
- `di/AppModule.kt` (ajustar para usar expect/actual)

### 3. Criar Implementações Web

Para `jsMain`, criar implementações simplificadas:
- Database usando IndexedDB ou LocalStorage
- AuthRepository usando LocalStorage
- AppModule ajustado para Web

## Como Executar

### Android
```bash
./gradlew :app:assembleDebug
```

### Web
```bash
./gradlew :app:jsBrowserDevelopmentWebpack
```

O build gerará os arquivos em `app/build/dist/js/productionExecutable/`

## Dependências Adicionadas

- `org.jetbrains.kotlin.multiplatform` - Plugin KMP
- `ktor-client-js` - Cliente HTTP para Web
- Compose Multiplatform (já incluído)

## Notas Importantes

1. **Room não funciona na Web**: Use SQLDelight ou uma implementação customizada com IndexedDB
2. **DataStore não funciona na Web**: Use LocalStorage (já implementado)
3. **Navigation Compose**: Funciona em ambas as plataformas
4. **Koin**: Funciona em ambas, mas precisa de configuração diferente

## Status Atual

✅ Configuração básica do KMP
✅ Expect/Actual para Storage, Database, Navigation, ViewModel
✅ Main.kt para Web criado
⏳ Migração de código para commonMain (em progresso)
⏳ Implementação completa do banco de dados Web
⏳ Testes em ambas as plataformas

