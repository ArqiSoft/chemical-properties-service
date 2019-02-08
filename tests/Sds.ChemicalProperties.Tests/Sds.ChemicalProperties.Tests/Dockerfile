FROM microsoft/dotnet:2.1-sdk AS build-env

ARG RID=linux-x64

WORKDIR /build

COPY Sds.ChemicalProperties.Domain/Sds.ChemicalProperties.Domain.csproj Sds.ChemicalProperties.Domain/
COPY Sds.ChemicalProperties.Tests/Sds.ChemicalProperties.Tests.csproj Sds.ChemicalProperties.Tests/

RUN dotnet restore Sds.ChemicalProperties.Tests/Sds.ChemicalProperties.Tests.csproj

COPY Sds.ChemicalProperties.Domain Sds.ChemicalProperties.Domain
COPY Sds.ChemicalProperties.Tests Sds.ChemicalProperties.Tests

RUN dotnet publish Sds.ChemicalProperties.Tests/Sds.ChemicalProperties.Tests.csproj -r $RID -c Release -o /dist

# Build runtime image
FROM microsoft/dotnet:2.1-sdk

LABEL maintainer="pshenichnov@gmail.com"

WORKDIR /app

RUN apt-get update && apt-get install -y curl
RUN curl https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh > /app/wait-for-it.sh && chmod 777 /app/wait-for-it.sh

COPY --from=build-env /dist ./

ENTRYPOINT ["dotnet", "vstest", "./Sds.ChemicalProperties.Tests.dll"]
