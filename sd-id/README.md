# Projecto de Sistemas Distribuídos #

## Primeira entrega ##

Grupo 04


75522 Daniel Sil \<daniel.sil@tecnico.ulisboa.pt\>

75714 Miguel Pasadinhas \<miguel.pasadinhas@tecnico.ulisboa.pt\>

76012 Carlos Carvalho \<carlosacarvalho@tecnico.ulisboa.pt\>


Repositório:
[tecnico-softeng-distsys-2015/A_14_04_52-project](https://github.com/tecnico-softeng-distsys-2015/A_14_04_52-project/)

-------------------------------------------------------------------------------

## Serviço: SD-ID 

### Instruções de instalação 

1. Iniciar sistema operativo
  * Linux ou Mac OS X (deveria funcionar em Windows mas não foi testado)

2. Iniciar servidores de apoio
  * JUDDI:
  ```sh
  sh $CATALINA_HOME/bin/startup.sh
  ```

3. Obter versão entregue
  ```sh
  git clone https://github.com/tecnico-softeng-distsys-2015/A_14_04_52-project.git
  cd A_14_04_52-project
  git checkout tags/SD-ID_R_1
  ```

4. Construir e executar **servidor**
  * A partir da pasta root do projecto:
  ```sh
  cd sd-id
  mvn compile
  mvn exec:java
  ```
  
5. Construir **cliente**
  * A partir da pasta root do projecto
  ```sh
  cd sd-id-cli
  mvn compile
  mvn exec:java
  ```
  
-------------------------------------------------------------------------------

### Instruções de teste: ###


1. Executar **testes do servidor**
  * A partir da root do projecto
  ```sh
  cd sd-id
  mvn test
  ```
  
2. Executar **testes do cliente**
  * A partir da root do projecto
  ```sh
  cd sd-id-cli
  mvn test
  ```
