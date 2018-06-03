package com.github.se7kn8.blockchaintest;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class FXMLController {

	@FXML
	private FlowPane chain;

	private BlockChain blockChain;

	private int counter;

	@FXML
	private void initialize() {
		blockChain = new BlockChain("First block", 2);
		blockChain.getBlocks().addListener((ListChangeListener.Change<? extends Block> change) -> {
			if (change.next()) {
				for (Block block : change.getAddedSubList()) {
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(ClassLoader.getSystemResource("fxml/BlockInfo.fxml"));
					loader.setController(new FXMLBlockController(block, "Block " + counter++));
					try {
						GridPane pane = loader.load();
						pane.setStyle("-fx-background-color: #B0BEC5; " +
								"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
								"-fx-background-radius: 10;");
						chain.getChildren().add(pane);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		Platform.runLater(() -> FXUtil.showInputDialog("Genesis block data?").ifPresent(data -> {
			Block block = blockChain.createGenesisBlock(data);
			Stage stage = FXUtil.showBlockGeneration(block);
			new Thread(() -> {
				block.mineBlock(blockChain.getDifficulty());
				Platform.runLater(() -> {
					stage.close();
					blockChain.addBlock(block);
				});
			}).start();
		}));
	}

	@FXML
	private void onAddBlock() {
		FXUtil.showInputDialog("Block data?").ifPresent(data -> {
			Block block = blockChain.createBlock(data);
			Stage stage = FXUtil.showBlockGeneration(block);
			new Thread(() -> {
				block.mineBlock(blockChain.getDifficulty());
				Platform.runLater(() -> {
					stage.close();
					blockChain.addBlock(block);
				});
			}).start();
		});
	}

	@FXML
	private void onValidateBlockChain() {
		// FIXME
		// if (blockChain.isChainValid()) {
		// 	 FXUtil.showInfoDialog("BlockChain is valid");
		// } else {
		//	 FXUtil.showInfoDialog("BlockChain is not valid");
		// }
	}

}
