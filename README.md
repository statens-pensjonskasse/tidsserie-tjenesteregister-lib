# tidsserie-bootstrap-lib

## Migreringsguide
### 2.0.x -> 3.0.0
Denne migreringen innebærer en endring av Maven-koordinater og pakkestruktur.
For å gjøre migreringen enklere kan du bruke følgende OpenRewrite-oppskrift.
Kopier den inn i `rewrite.yml` i rotmappen til prosjektet ditt.
Intellij IDEA har støtte for å kjøre OpenRewrite-oppskrifter så da kan du trykke på play-knappen for å kjøre migreringen.

```yaml
---
type: specs.openrewrite.org/v1beta/recipe
name: no.spk.premie.UpdateTjenesteregisterLib
displayName: Update tjenesteregister coordinates and package structures
description: Updates Maven coordinates and package structures for tjenesteregister-lib
recipeList:


  # Move     
  - org.openrewrite.maven.ChangeDependencyGroupIdAndArtifactId:
      oldGroupId: no.spk.pensjon.faktura
      oldArtifactId: faktura-tjenesteregister-lib
      newGroupId: no.spk.tidsserie
      newArtifactId: tjenesteregister-lib
      overrideManagedVersion: true
      newVersion: 3.0.0

  - org.openrewrite.java.ChangePackage:
      oldPackageName: no.spk.pensjon.faktura.tjenesteregister
      newPackageName: no.spk.tidsserie.tjenesteregister
      recursive: true
      
```
