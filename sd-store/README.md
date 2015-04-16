# Bubble Docs SD-Store

# Projecto de Sistemas Distribuídos #

## Primeira entrega ##

Grupo de SD 52

Rodrigo Vieira, 70315, rvrodrigo1@hotmail.com

Francisco Gonçalves, 76547, joao.francisco.vieira.goncalves@tecnico.ulisboa.pt

Teresa Coelho, 75453, teresa.coelho@tecnico.ulisboa.pt


Repositório:
[tecnico-softeng-distsys-2015/A_14_04_52-project](https://github.com/tecnico-softeng-distsys-2015/A_14_04_52-project/)


-------------------------------------------------------------------------------

## Serviço SD-STORE 

### Instruções de instalação 

[0] Iniciar sistema operativo

Windows

[1] Iniciar servidores de apoio

JUDDI:
> startup.bat

[2] Criar pasta temporária

> mkdir ProjSD1
>> cd ProjSD1

[3] Obter versão entregue

> git clone https://github.com/tecnico-softeng-distsys-2015/A_14_04_52-project.git R_3

[4] Construir e executar **servidor**

> cd A_14_04_52/sd-store
>> mvn clean package 
>>> mvn exec:java


[5] Construir **cliente**

> cd A_14_04_52/sd-store-cli
>> mvn clean package

-------------------------------------------------------------------------------

### Instruções de teste: ###

[1] Executar **cliente de testes**

> cd A_14_04_52/sd-store-cli
>> mvn test


[2] Executar **testes de servidor**

> cd A_14_04_52/sd-store
>> mvn test

-------------------------------------------------------------------------------
**FIM**
