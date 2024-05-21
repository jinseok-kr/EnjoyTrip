package com.ssafy.trip.board.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.trip.board.model.BoardDto;
import com.ssafy.trip.board.model.service.BoardService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/board")
public class BoardController {

	private BoardService boardservice;

	public BoardController(BoardService boardservice) {
		super();
		this.boardservice = boardservice;
	}

	@GetMapping("/")
	public ResponseEntity<Page<BoardDto>> getAllBoards(@RequestParam(value = "offset", defaultValue = "0") int offset, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) throws SQLException {
		return ResponseEntity.ok().body(boardservice.boardList(offset, pageSize));
	}

	@RequestMapping(value="/", method = RequestMethod.POST, produces =  "application/json", consumes = "multipart/form-data")
	public ResponseEntity<?> writeBoard(@RequestPart(value="board") BoardDto board, @RequestPart(value = "img", required = false) MultipartFile img) throws SQLException, IOException  {
		boardservice.registBoard(board, img);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getBoard(@PathVariable("id") int id) throws SQLException {
		BoardDto board = boardservice.getBoardWithHit(id);
		return ResponseEntity.ok().body(board);
	}

	@RequestMapping(value="/", method = RequestMethod.PATCH, produces =  "application/json", consumes = "multipart/form-data")
	public ResponseEntity<?> updateBoard(@RequestPart(value="board") BoardDto board, @RequestPart(value = "img", required = false) MultipartFile img) throws SQLException, IOException {
		boardservice.updateBoard(board, img);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteBoard(@PathVariable("id") int id)
			throws SQLException {
		boardservice.deleteBoard(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/")
	public ResponseEntity<?> deleteBoards(@RequestParam(value = "del-board", required = false) int[] ids)
			throws SQLException {
		boardservice.deleteBoards(ids);
		return ResponseEntity.ok().build();
	}

}
