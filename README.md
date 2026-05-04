# DartCore

## Build
1. Build Frontend
```bash
cd frontend
npm run build
```
2. Build Backend
```bash
cd backend
gradle build -x test
```
3. Edit `.env` 
4. Run Docker
```bash
docker compose --build -d
```