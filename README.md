[![Build status](https://ci.appveyor.com/api/projects/status/fdmnwld8fvj6sksr?svg=true)](https://ci.appveyor.com/project/k-emiko/aqa3-2-ex1)

### Оговорки по запуску тестов
Данные тесты запускают SUT и базу данных при помощи testcontainers прямо из тестов, поэтому [docker-compose.yml](https://github.com/k-emiko/aqa3-2-ex1/blob/master/artifacts/docker-compose.yml) для их работы не требуется.
Однако условия задания требуют его предоставить, поэтому он есть в данном репозитории для демонстрации моего понимания того, как он должен быть настроен. 

Также в этом [коммите](https://github.com/k-emiko/aqa3-2-ex1/tree/74bf85c59306fb146b157c6a6748b6350089fb1a) можно посмотреть работающий сетап без использования testcontainers.
