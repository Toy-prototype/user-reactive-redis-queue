.PHONY: up
up:
	@echo "Running docker-compose up -d"
	@docker-compose up -d

.PHONY: down
down:
	@echo "Running docker-compose down"
	@docker-compose down --remove-orphans