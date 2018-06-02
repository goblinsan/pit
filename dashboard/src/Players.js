import React from "react";
import {Table} from "react-bootstrap";

class PlayerRow extends React.Component {

    render() {
        const player = this.props.player;
        const image = "images/" + this.props.image;
        return (
            <tr style={{'backgroundColor' : player.connected ? 'green' : 'white'}}>
                <td><img src={image} width={50} alt={"avatar for player: " + player.name}/></td>
                <td>{player.name}</td>
                <td>{player.score}</td>
            </tr>
        );
    }
}

class PlayerTable extends React.Component {
    render() {
        const rows = [];

        this.props.players.forEach((player, index) => {
            rows.push(
                <PlayerRow
                    image={String(index + 1) + '.png'}
                    player={player}
                    key={player.name}/>
            );
        });

        return (
            <Table>
                <thead>
                <tr>
                    <th></th>
                    <th>Name</th>
                    <th>Score</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </Table>
        );
    }
}

export default PlayerTable;