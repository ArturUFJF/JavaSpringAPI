package com.devsuperior.dslist.services;

import com.devsuperior.dslist.dto.GameListDTO;
import com.devsuperior.dslist.entities.GameList;
import com.devsuperior.dslist.projections.GameMinProjection;
import com.devsuperior.dslist.repositories.GameListRepository;
import com.devsuperior.dslist.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameListService {

    @Autowired //"Injeta" o repositório
    private GameListRepository gameListRepository;
    @Autowired
    private GameRepository gameRepository;

    @Transactional(readOnly = true)
    public List<GameListDTO> findAll(){
        List<GameList> result = gameListRepository.findAll();
        List<GameListDTO> dto = result.stream().map(x -> new GameListDTO(x)).toList();

        return dto;
    }

    @Transactional
    public void move(Long listId, int sourceIndex, int destinationIndex) {

        // Recupera a lista de jogos na ordem correta
        List<GameMinProjection> list = gameRepository.searchByList(listId);

        // Remove o objeto da posição original
        GameMinProjection obj = list.remove(sourceIndex);

        // Adiciona o objeto na nova posição
        list.add(destinationIndex, obj);

        // Determina o intervalo de atualização
        int min = Math.min(sourceIndex, destinationIndex);
        int max = Math.max(sourceIndex, destinationIndex);

        // Atualiza as posições no banco de dados
        for (int i = min; i <= max; i++) {
            GameMinProjection game = list.get(i);
            gameListRepository.updateBelongingPosition(listId, game.getId(), i);
        }
    }

}
