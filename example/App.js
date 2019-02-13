import React, { Component } from "react";
import { Button, StyleSheet, Text, View } from "react-native";
import GetSocial from "react-native-get-social";

export default class App extends Component {
  testShowInviteUI = async () => {
    console.log(GetSocial.showInviteUI());
  };

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native GetSocial</Text>
        <Text style={styles.instructions}>To get started push the button!</Text>
        <Button title="Send invite" onPress={this.testShowInviteUI} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F5FCFF"
  },
  welcome: {
    fontSize: 20,
    textAlign: "center",
    margin: 10
  },
  instructions: {
    textAlign: "center",
    color: "#333333",
    marginBottom: 5
  }
});
