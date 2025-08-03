# Sistema de Testes com ZooKeeper e Leader Election

Este projeto configura um cluster ZooKeeper com múltiplos nós e expõe uma API para realizar ações específicas como **Leader Election**, **Locks**, **Queues** e **Barriers**.

---

## 🚀 Executando o Ambiente

### 1. Subir o cluster ZooKeeper e os nós de aplicação
Execute o comando abaixo para iniciar todos os serviços:

```bash
docker-compose up -d --build
```

### Executando ação no nó 

```bash
curl --request POST \
  --url http://localhost:8081/leader/elect \
  --header 'Content-Type: application/json' \
  --data '["SyncPrimitive","localhost"]'
```