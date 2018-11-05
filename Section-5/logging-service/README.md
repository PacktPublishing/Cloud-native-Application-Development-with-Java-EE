# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 5.2: Adding good, detailed and structured logging

Having good, detailed and structured logging is the cornerstone of good and
easy diagnosability.

### Step 1: Define a suitable logging strategy

- **DEBUG** - Used for detailed and valuable information required for debugging.
- **INFO** - Used for important runtime or business events.
- **WARNING** - Used for non critical errors that can be compensated by the system.
- **ERROR** -  Used for critical errors that need immediate attention (ops team or SRE).

### Step 2: Choose a suitable logging framework


### Step 3: Configure logging framework

1. **Reduce noise!** Avoid of excessive logging by each and every 3rd party dependency.
2.


### Bonus Step: The Elastic Stack

As a bonus, you may want to follow the instructions found in the elastic stack
repository (https://github.com/elastic/stack-docker) to fire up a current Elastic
Stack locally using Docker Compose. Careful, you need quite some resources.
