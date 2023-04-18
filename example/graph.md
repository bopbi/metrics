```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph example
    app
  end
  subgraph domain
    sample-domain
  end
  subgraph feature
    other-feature
    sample-feature
  end
  subgraph utility
    view
  end
  app --> sample-feature
  app --> other-feature
  other-feature --> view
  sample-feature --> sample-domain

```
