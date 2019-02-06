FROM microsoft/dotnet:2.1-runtime AS base
WORKDIR /app

FROM microsoft/dotnet:2.1-sdk AS build
WORKDIR /src
COPY Sds.ChemicalProperties.Tests/Sds.ChemicalProperties.Tests.csproj Sds.ChemicalProperties.Tests/
RUN dotnet restore Sds.ChemicalProperties.Tests/Sds.ChemicalProperties.Tests.csproj
COPY . .
WORKDIR /src/Sds.ChemicalProperties.Tests
RUN dotnet build Sds.ChemicalProperties.Tests.csproj -c Release -o /app

FROM build AS publish
RUN dotnet publish Sds.ChemicalProperties.Tests.csproj -c Release -o /app

FROM base AS final
WORKDIR /app
COPY --from=publish /app .
ENTRYPOINT ["dotnet", "Sds.ChemicalProperties.Tests.dll"]
